package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.theme.lumo.LumoIcon;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.keys.ContractProductKey;
import cz.upce.fei.dt.beckend.exceptions.ResourceNotFoundException;
import cz.upce.fei.dt.beckend.services.*;
import cz.upce.fei.dt.ui.components.ContactAccordion;
import cz.upce.fei.dt.ui.components.PriceFieldWithButton;
import cz.upce.fei.dt.ui.components.forms.events.UpdateContractPriceEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class ContractForm extends FormLayout implements IEditForm<Contract> {
    private final BeanValidationBinder<Contract> binder = new BeanValidationBinder<>(Contract.class);
    private final ComboBox<Contact> contactCB = new ComboBox<>("Klient");
    private final ContactAccordion contactAccordion = new ContactAccordion();
    private final MultiSelectComboBox<Product> productsMSB = new MultiSelectComboBox<>("Objednané Produkty");
    private final FormLayout contractProductFormsLayout = new FormLayout();
    private final HashMap<Long, ContractProductForm> contractProductForms = new HashMap<>();
    private final PriceFieldWithButton priceField = new PriceFieldWithButton("Cena s marží", VaadinIcon.EDIT);
    private final DeadlineForm deadlineForm;
    private final DeadlineService deadlineService;
    private final NoteService noteService;
    private NoteForm noteForm;
    private final FileService fileService;
    private FileForm fileForm;
    private Contract contract;
    private final Button reloadProducts = new Button(LumoIcon.RELOAD.create());

    public ContractForm(
            ContactService contactService,
            ProductService productService,
            NoteService noteService,
            FileService fileService,
            DeadlineService deadlineService,
            StatusService statusService) {
        setClassName("edit-form");

        this.noteService = noteService;
        this.fileService = fileService;
        this.deadlineService = deadlineService;
        deadlineForm = new DeadlineForm(deadlineService, statusService);

        setupContactCB(contactService);
        setupProductMSB(productService);
        setupContractProductsForms();
        setupPriceField();
        setupReloadProducts();

        ComponentUtil.addListener(UI.getCurrent(), UpdateContractPriceEvent.class, this::updatePrice);

        this.setColspan(contactCB, 2);
        this.setColspan(contactAccordion, 2);
        this.setColspan(productsMSB, 2);
        this.setColspan(priceField, 2);
        this.setColspan(deadlineForm, 2);
        this.setColspan(contractProductFormsLayout, 2);
        add(contactCB, contactAccordion, productsMSB, contractProductFormsLayout, priceField, deadlineForm);
    }

    private void setupReloadProducts() {
        reloadProducts.addThemeVariants(ButtonVariant.LUMO_SMALL);
        reloadProducts.addClickListener(_ -> {
            Set<Product> selectedProducts = productsMSB.getSelectedItems();
            HashMap<Long, Double> productionPriceMap = new HashMap<>();
            selectedProducts.forEach(product -> productionPriceMap.put(product.getId(), product.getProductionPrice()));
            contractProductForms.forEach((key, form) -> {
                ContractProduct contractProduct = form.getValue();
                contractProduct.setPricePerPiece(productionPriceMap.get(key));
                form.setValue(contractProduct);
            });
            updatePrice(null);
        });
    }


    //region Setups
    private void setupContractProductsForms() {
        contractProductFormsLayout.setWidthFull();
        contractProductFormsLayout.setClassName("contract-products-layout");
        contractProductFormsLayout.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("300px", 2),
                new ResponsiveStep("450px", 3),
                new ResponsiveStep("600px", 4),
                new ResponsiveStep("750px", 5),
                new ResponsiveStep("900px", 6)
        );
    }

    private void setupPriceField() {
        priceField.setPrefixComponent(reloadProducts);
        priceField.setReadOnly(true);
        priceField.button.addClickListener(_ -> {
            contract.setOwnPrice(priceField.isReadOnly());
            priceField.setReadOnly(!priceField.isReadOnly());
            updatePrice(null);
        });
        binder.forField(priceField)
                .withValidator(new DoubleRangeValidator("Cena mimo hodnoty", 0.0, Double.MAX_VALUE))
                .asRequired()
                .bind(Contract::getPrice, Contract::setPrice);
    }

    private void updatePrice(UpdateContractPriceEvent event) {
        if (priceField.isReadOnly() && contractProductForms != null && !contractProductForms.isEmpty()) {
            double price = 0;
            for (ContractProductForm form : contractProductForms.values()) {
                try {
                    form.validate();
                } catch (ValidationException ex) {
                    Notification.show(ex.getValidationErrors().toString()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                Double pricePerPiece = form.getValue().getPricePerPiece();
                Integer amount = form.getValue().getAmount();
                price += pricePerPiece * amount;
            }
            priceField.setValue(price);
        }
    }

    private void setupProductMSB(ProductService productService) {
        productsMSB.setItems(query -> productService.findAllByName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        productsMSB.setItemLabelGenerator(Product::getName);
        productsMSB.setRequired(true);
        productsMSB.addSelectionListener(this::addProductComponentForm);
    }

    private void setupContactCB(ContactService contactService) {
        contactCB.setItems(query ->
                contactService.findAllByIcoOrClientOrEmailOrPhone(query.getPage(), query.getPageSize(), query.getFilter().orElse(""))
        );

        contactCB.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                try {
                    contactAccordion.setContact(contactService.findById(event.getValue().getId()));
                } catch (ResourceNotFoundException exception) {
                    Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else
                contactAccordion.setContact(null);
        });
        contactCB.setItemLabelGenerator(this::getContactLabel);
        binder.forField(contactCB)
                .asRequired()
                .bind(Contract::getContact, Contract::setContact);
    }

    private void addProductComponentForm(MultiSelectionEvent<MultiSelectComboBox<Product>, Product> event) {
        event.getAddedSelection().forEach(product -> {
            if (!contractProductForms.containsKey(product.getId())) {
                ContractProductForm form = new ContractProductForm(
                        ContractProduct.builder()
                                .id(new ContractProductKey(contract.getId(), product.getId()))
                                .amount(1)
                                .pricePerPiece(product.getSellingPrice())
                                .product(product)
                                .contract(contract)
                                .build());
                contractProductForms.put(product.getId(), form);
                contractProductFormsLayout.add(form);
            } else {
                contractProductFormsLayout.add(contractProductForms.get(product.getId()));
            }
        });
        event.getRemovedSelection().forEach(product ->
                contractProductFormsLayout.remove(contractProductForms.remove(product.getId()))
        );
        updatePrice(null);
    }

    private String getContactLabel(Contact contact) {
        return contact.getClient() + ", IČO:" + contact.getICO();
    }

    //endregion

    //region IEditForm
    @Override
    public Contract getValue() {
        contract.getContractProducts().clear();
        contractProductForms.forEach((_, form) ->
                contract.getContractProducts().add(form.getValue()));
        contract.setDeadlines(Set.of(deadlineForm.getDeadline()));

        return contract;
    }

    @Override
    public void setValue(Contract value) {
        productsMSB.clear();
        contract = value;

        if (contract != null) {
            contract.getContractProducts().forEach(contractProduct ->
                    contractProductForms.put(
                            contractProduct.getId().getProductId(),
                            new ContractProductForm(contractProduct))
            );

            productsMSB.setValue(contract.getSelectedProducts());
            priceField.setReadOnly(!contract.getOwnPrice());
            deadlineForm.setValue(deadlineService.findFirstByContractIdOrderByCreatedDesc(contract.getId()));

            if (contract.getId() != null) {
                addFileForm();
                addNoteForm();
            }
        } else {
            if (fileForm != null) {
                remove(fileForm);
            }
            fileForm = null;
            if (noteForm != null) {
                remove(noteForm);
            }
            noteForm = null;
        }
        binder.readBean(contract);
    }

    private void addFileForm() {
        if (fileForm != null)
            remove(fileForm);
        fileForm = new FileForm(fileService, contract);
        this.add(fileForm);
    }

    private void addNoteForm() {
        if (noteForm != null)
            remove(noteForm);
        noteForm = new NoteForm(noteService, Contract.builder().id(contract.getId()).build());
        this.add(noteForm);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(contract);
        if (contractProductForms.isEmpty()) {
            productsMSB.setHelperText("Vyberte produkt.");
            throw new ValidationException(Collections.emptyList(), Collections.emptyList());
        }
        for (ContractProductForm form : contractProductForms.values()) {
            form.validate();
        }
        deadlineForm.validate();
    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
