package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.ContractProduct;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.keys.ContractProductKey;
import cz.upce.fei.dt.beckend.services.*;
import cz.upce.fei.dt.ui.components.forms.events.FileForm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class ContractForm extends FormLayout implements IEditForm<Contract> {
    private final BeanValidationBinder<Contract> binder = new BeanValidationBinder<>(Contract.class);
    private final ComboBox<Contact> contactCB = new ComboBox<>("Klient");
    private final MultiSelectComboBox<Product> productsMSB = new MultiSelectComboBox<>("Objednané Produkty");
    private final FormLayout contractProductFormsLayout = new FormLayout();
    private final HashMap<Long, ContractProductForm> contractProductForms = new HashMap<>();
    private final DeadlineForm deadlineForm;
    private final DeadlineService deadlineService;
    private final NoteService noteService;
    private NoteForm noteForm;
    private final FileService fileService;
    private FileForm fileForm;
    private Contract contract;

    public ContractForm(ContactService contactService, ProductService productService, NoteService noteService, FileService fileService, DeadlineService deadlineService) {
        setClassName("edit-form");

        this.noteService = noteService;
        this.fileService = fileService;
        this.deadlineService = deadlineService;
        deadlineForm = new DeadlineForm(deadlineService);

        setupContactCB(contactService);
        setupProductMSB(productService);
        setupContractProductsForms();

        this.setColspan(productsMSB, 3);
        this.setColspan(deadlineForm, 6);
        this.setColspan(contractProductFormsLayout, 3);
        add(contactCB, productsMSB, contractProductFormsLayout, deadlineForm);
    }

    //region Setups
    private void setupContractProductsForms() {
        contractProductFormsLayout.setWidthFull();
        contractProductFormsLayout.setClassName("contract-products-layout");
        contractProductFormsLayout.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("200px", 2),
                new ResponsiveStep("400px", 3),
                new ResponsiveStep("600px", 4),
                new ResponsiveStep("800px", 5),
                new ResponsiveStep("1000px", 6)
        );
    }

    private void setupProductMSB(ProductService productService) {
        productsMSB.setItems(query -> productService.findAllProductsIdAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        productsMSB.setItemLabelGenerator(Product::getName);
        productsMSB.setRequired(true);
        productsMSB.addSelectionListener(this::addProductComponentForm);
    }

    private void setupContactCB(ContactService contactService) {
        contactCB.setItems(query ->
                contactService.findAllContactsIdAndICOAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse(""))
        );
        contactCB.setItemLabelGenerator(this::getContactLabel);
        this.setColspan(contactCB, 3);
        binder.forField(contactCB)
                .asRequired()
                .bind(Contract::getContact, Contract::setContact);
    }
    private void addProductComponentForm(MultiSelectionEvent<MultiSelectComboBox<Product>, Product> event) {
        productsMSB.setHelperText("");
        event.getAddedSelection().forEach(product -> {
            if (!contractProductForms.containsKey(product.getId())) {
                ContractProductForm form = new ContractProductForm(
                        ContractProduct.builder()
                                .id(new ContractProductKey(contract.getId(), product.getId()))
                                .amount(1)
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
    }

    private String getContactLabel(Contact contact) {
        return contact.getName() + ", IČO:" + contact.getICO();
    }

    //endregion

    //region IEditForm
    @Override
    public Contract getValue() {
        contract.getContractProducts().clear();
        contractProductForms.forEach((key, form) ->
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
