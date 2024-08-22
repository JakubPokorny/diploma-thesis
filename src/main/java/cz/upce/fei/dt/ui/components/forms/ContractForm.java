package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.theme.lumo.LumoIcon;
import cz.upce.fei.dt.backend.dto.IExtraCost;
import cz.upce.fei.dt.backend.entities.Contact;
import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.ContractProduct;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.keys.ContractProductKey;
import cz.upce.fei.dt.backend.exceptions.ResourceNotFoundException;
import cz.upce.fei.dt.backend.services.*;
import cz.upce.fei.dt.backend.utilities.CzechI18n;
import cz.upce.fei.dt.ui.components.ContactAccordion;
import cz.upce.fei.dt.ui.components.forms.events.UpdateContractFinancialBalance;
import cz.upce.fei.dt.ui.components.forms.fields.DescriptionField;
import cz.upce.fei.dt.ui.components.forms.fields.PriceField;
import cz.upce.fei.dt.ui.utilities.CustomComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class ContractForm extends FormLayout implements IEditForm<Contract> {
    private final BeanValidationBinder<Contract> binder = new BeanValidationBinder<>(Contract.class);
    private final ComboBox<Contact> contactCB = new ComboBox<>("Klient");
    private final DescriptionField noteField = new DescriptionField(null, Contract.MAX_DESCRIPTION_LENGTH);
    private final PriceField invoicePriceField = new PriceField("Fakturovaná částka");
    private final PriceField totalCostField = new PriceField("Celkové náklady");
    private final PriceField totalProfitField = new PriceField("Celkový zisk");
    private final PriceField totalPercentageProfit = new PriceField("Celková marže");
    private final MultiSelectComboBox<Product> productsMSB = new MultiSelectComboBox<>("Objednané Produkty");
    private final DatePicker finalDeadline = new DatePicker("Konečný termín");

    private CommentForm commentForm;
    private final DeadlineForm deadlineForm;
    private FileForm fileForm;
    private ExtraCostForm extraCostForm;
    private final FormLayout contractProductFormsLayout = new FormLayout();
    private final FormLayout financialBalanceForm = new FormLayout();

    private final Button updateProductsPrices = new Button(LumoIcon.RELOAD.create());

    private final Details noteDetail;
    private final Details productsDetail;
    private final Details extraCostsDetail;
    private final Details financialDetail;
    private final Details deadlinesDetail;
//    private final Details commentsDetail;
//    private final Details filesDetail;


    private final ContactAccordion contactAccordion = new ContactAccordion();
    private final HashMap<Long, ContractProductForm> contractProductForms = new HashMap<>();

    private final DeadlineService deadlineService;
    private final CommentService commentService;
    private final FileService fileService;
    private final ExtraCostService extraCostService;

    private Contract contract;
    private IExtraCost iExtraCost;

    public ContractForm(
            ContactService contactService,
            ProductService productService,
            CommentService commentService,
            FileService fileService,
            DeadlineService deadlineService,
            StatusService statusService,
            ExtraCostService extraCostService) {

        setClassName("edit-form");

        this.commentService = commentService;
        this.fileService = fileService;
        this.deadlineService = deadlineService;
        this.extraCostService = extraCostService;

        deadlineForm = new DeadlineForm(deadlineService, statusService);
        extraCostForm = new ExtraCostForm(extraCostService);

        productsDetail = new Details("Objednané Produkty", productsMSB, contractProductFormsLayout);
        noteDetail = new Details("Poznámky", noteField);
        extraCostsDetail = new Details("Více náklady", extraCostForm);
        financialDetail = new Details("Finanční bilance", financialBalanceForm);
        deadlinesDetail = new Details("Termíny", finalDeadline, deadlineForm);
//        commentsDetail = new Details("Komentáře");
//        filesDetail = new Details("Soubory");
        setupDetails();

        setupContactCB(contactService);
        setupDescriptionDetail();
        setupProductMSB(productService);
        setupContractProductsForms();
        setupFinancialBalance();
        setupReloadProducts();
        setupFinalDeadline();

        ComponentUtil.addListener(UI.getCurrent(), UpdateContractFinancialBalance.class, this::updateFinancialBalance);

        this.setColspan(contactCB, 2);
        this.setColspan(contactAccordion, 2);
        this.setColspan(noteDetail, 2);
        this.setColspan(productsDetail, 2);
        this.setColspan(extraCostsDetail, 2);
        this.setColspan(financialDetail, 2);
        this.setColspan(deadlinesDetail, 2);

        add(contactCB, contactAccordion,
                noteDetail,
                productsDetail,
                extraCostsDetail,
                financialDetail,
                deadlinesDetail
//                filesDetail,
//                commentsDetail
        );
    }


    //region Setups
    private void setupDetails() {
        productsDetail.setOpened(true);

        financialDetail.setOpened(true);

        deadlinesDetail.setOpened(true);
    }

    //region Contact
    private void setupContactCB(ContactService contactService) {
        contactCB.setItems(contactService::findAllByIcoOrClientOrEmailOrPhone);

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

    private String getContactLabel(Contact contact) {
        return contact.getClient() + ", IČO:" + contact.getICO();
    }
    //endregion

    private void setupDescriptionDetail() {
        noteField.setMaxLength(Contract.MAX_DESCRIPTION_LENGTH);
        binder.forField(noteField).bind(Contract::getNote, Contract::setNote);
    }

    //region Contract Products
    private void setupProductMSB(ProductService productService) {
        productsMSB.setWidthFull();
        productsMSB.setItems(productService::findAllByName);
        productsMSB.setItemLabelGenerator(Product::getName);
        productsMSB.setRequired(true);
        productsMSB.addSelectionListener(this::addContractProductForm);
    }

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

    private void addContractProductForm(MultiSelectionEvent<MultiSelectComboBox<Product>, Product> event) {
        event.getAddedSelection().forEach(product -> {
            if (!contractProductForms.containsKey(product.getId())) {
                ContractProductForm form = new ContractProductForm(
                        ContractProduct.builder()
                                .id(new ContractProductKey(contract.getId(), product.getId()))
                                .amount(1)
                                .productionPricePerPiece(product.getProductionPrice())
                                .sellingPricePerPiece(product.getSellingPrice())
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
        updateFinancialBalance(null);
    }
    //endregion

    //region Financial Balance
    private void setupFinancialBalance() {
        invoicePriceField.setReadOnly(true);
        Button editInvoicePriceField = CustomComponent.createSmallTertiaryButton(
                VaadinIcon.EDIT.create(), _ -> {
                    contract.setOwnInvoicePrice(invoicePriceField.isReadOnly());
                    invoicePriceField.setReadOnly(!invoicePriceField.isReadOnly());
                    if (invoicePriceField.isReadOnly())
                        updateFinancialBalance(null);
                }
        );
        invoicePriceField.addValueChangeListener(_ -> {
            if (!invoicePriceField.isReadOnly())
                updateFinancialBalance(null);
        });

        invoicePriceField.suffixLayout.add(editInvoicePriceField);
        binder.forField(invoicePriceField)
                .withValidator(new DoubleRangeValidator("Cena mimo hodnoty", 0.0, Double.MAX_VALUE))
                .asRequired()
                .bind(Contract::getInvoicePrice, Contract::setInvoicePrice);

        totalCostField.setReadOnly(true);
        binder.forField(totalCostField)
                .asRequired()
                .bind(Contract::getTotalCost, Contract::setTotalCost);

        totalProfitField.setReadOnly(true);
        binder.forField(totalProfitField)
                .asRequired()
                .bind(Contract::getTotalProfit, Contract::setTotalProfit);

        totalPercentageProfit.setReadOnly(true);
        totalPercentageProfit.round = false;
        totalPercentageProfit.currencySuffix.setText("%");

        financialBalanceForm.setWidthFull();
        financialBalanceForm.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("300px", 3)
        );
        financialBalanceForm.setColspan(invoicePriceField, 2);

        financialBalanceForm.add(
                invoicePriceField,
                updateProductsPrices,
                totalCostField,
                totalProfitField,
                totalPercentageProfit
        );
    }

    private void setupReloadProducts() {
        updateProductsPrices.setText("Aktualizovat ceny produktů");
        updateProductsPrices.addClickListener(_ -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Aktualizovat ceny");
            dialog.setText("Opravdu chcete aktualizovat ceny produktů?");

            dialog.setCancelable(true);
            dialog.setCancelText("Zrušit");

            dialog.setConfirmText("Aktualizovat");
            dialog.addConfirmListener(_ -> reloadProducts());
            dialog.open();
        });
    }

    private void reloadProducts() {
        HashMap<Long, Double> productionPrices = new HashMap<>();
        HashMap<Long, Double> sellingPrices = new HashMap<>();

        productsMSB.getSelectedItems().forEach(product -> {
            productionPrices.put(product.getId(), product.getProductionPrice());
            sellingPrices.put(product.getId(), product.getSellingPrice());
        });

        contractProductForms.forEach((key, form) -> {
            ContractProduct contractProduct = form.getValue();
            contractProduct.setSellingPricePerPiece(sellingPrices.get(key));
            contractProduct.setProductionPricePerPiece(productionPrices.get(key));
            form.setValue(contractProduct);
        });

        updateFinancialBalance(null);
        Notification.show("Ceny aktualizovány, ulož změny.");
    }

    private void updateFinancialBalance(UpdateContractFinancialBalance event) {
        double totalExtraCost = 0;
        double invoicePrice = 0;
        double totalCost = 0;
        double totalProfit;

        iExtraCost = extraCostService.countByContractId(contract);
        if (iExtraCost != null) {
            totalExtraCost = iExtraCost.getTotalExtraCost();
            extraCostsDetail.setSummaryText(String.format("Více náklady (%s): %sKč", iExtraCost.getCount(), Math.round(totalExtraCost)));
        } else
            extraCostsDetail.setSummaryText("Více náklady (0): 0Kč");

        if (contractProductForms != null && !contractProductForms.isEmpty()) {
            for (ContractProductForm form : contractProductForms.values()) {
                try {
                    form.validate();
                } catch (ValidationException ex) {
                    Notification.show(ex.getValidationErrors().toString()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
                Integer amount = form.getValue().getAmount();
                invoicePrice += form.getValue().getSellingPricePerPiece() * amount;
                totalCost += form.getValue().getProductionPricePerPiece() * amount;
            }
            if (invoicePriceField.isReadOnly())
                invoicePriceField.setValue(invoicePrice);
            else
                invoicePrice = invoicePriceField.getValue();
        }
        totalCost += totalExtraCost;
        totalProfit = invoicePrice - totalCost;
        totalCostField.setValue(totalCost);
        totalProfitField.setValue(totalProfit);
        if (totalCost != 0)
            totalPercentageProfit.setValue((double) Math.round(((invoicePrice / (totalCost / 100)) - 100) * 100) / 100);
        else
            totalPercentageProfit.setValue(0.0);
    }
    //endregion

    private void setupFinalDeadline() {
        finalDeadline.setWidthFull();
        finalDeadline.setI18n(CzechI18n.getDatePickerI18n());
        finalDeadline.setClearButtonVisible(true);

        binder.forField(finalDeadline).bind(Contract::getFinalDeadline, Contract::setFinalDeadline);
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

        if (contract != null) { // new or update
            contract.getContractProducts().forEach(contractProduct ->
                    contractProductForms.put(
                            contractProduct.getId().getProductId(),
                            new ContractProductForm(contractProduct))
            );

            productsMSB.setValue(contract.getSelectedProducts());
            invoicePriceField.setReadOnly(!contract.getOwnInvoicePrice());
            deadlineForm.setValue(deadlineService.findFirstByContractIdOrderByCreatedDesc(contract.getId()));

            if (contract.getId() != null) { // just for update
                extraCostsDetail.setEnabled(true);
                extraCostForm.setContract(contract);
                addFileForm();
                addNoteForm();
            } else { // just for new
                extraCostsDetail.setEnabled(false);
                extraCostsDetail.setOpened(false);
            }
        } else { // close
            if (fileForm != null) {
                remove(fileForm);
            }
            fileForm = null;
            if (commentForm != null) {
                remove(commentForm);
            }
            commentForm = null;
        }

        binder.readBean(contract);
    }

    private void addFileForm() {
        if (fileForm != null)
            remove(fileForm);
        fileForm = new FileForm(fileService, contract);
        this.add(fileForm);
//        filesDetail.add(fileForm);
    }

    private void addNoteForm() {
        if (commentForm != null)
            remove(commentForm);
        commentForm = new CommentForm(commentService, Contract.builder().id(contract.getId()).build());
        this.add(commentForm);
//        commentsDetail.add(commentForm);
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
