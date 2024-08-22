package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.ExtraCost;
import cz.upce.fei.dt.backend.entities.ExtraCost_;
import cz.upce.fei.dt.backend.services.ExtraCostService;
import cz.upce.fei.dt.ui.components.forms.events.UpdateContractFinancialBalance;
import cz.upce.fei.dt.ui.components.forms.fields.PriceField;
import cz.upce.fei.dt.ui.utilities.CustomComponent;
import cz.upce.fei.dt.ui.utilities.CustomNotification;

import java.util.stream.Stream;

public class ExtraCostForm extends FormLayout {
    private final ExtraCostService extraCostService;

    private final Binder<ExtraCost> binder = new BeanValidationBinder<>(ExtraCost.class);
    private ExtraCost extraCost = new ExtraCost();
    private Contract contract;
    private CallbackDataProvider<ExtraCost, Void> dataProvider;

    private final PriceField extraCostField = new PriceField("Vícenáklad");
    private final TextField descriptionField = new TextField("Popis");
    private final GridPro<ExtraCost> gridPro = new GridPro<>();

    private final Button saveButton = new Button("Uložit");

    public ExtraCostForm(ExtraCostService extraCostService) {
        this.extraCostService = extraCostService;

        setupExtraCostField();
        setupDescriptionField();
        setupSaveButton();
        setupGridPro();

        this.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 5)
        );
        this.setColspan(descriptionField, 3);
        this.setColspan(gridPro, 5);

        this.add(extraCostField, descriptionField, saveButton, gridPro);
    }


    //region setups
    public void setupExtraCostField() {
        binder.forField(extraCostField)
                .withValidator(new DoubleRangeValidator("Vícenáklad mimo hodnoty", 0.0, Double.MAX_VALUE))
                .asRequired()
                .bind(ExtraCost::getExtraCost, ExtraCost::setExtraCost);
    }

    public void setupDescriptionField() {
        binder.forField(descriptionField)
                .asRequired()
                .bind(ExtraCost::getDescription, ExtraCost::setDescription);
    }

    private void setupSaveButton() {
        saveButton.addClickListener(_ -> {
            try {
                binder.writeBean(extraCost);
                extraCost.setContract(contract);
                extraCostService.save(extraCost);
                extraCost = new ExtraCost();
                binder.readBean(extraCost);
                dataProvider.refreshAll();
//                ComponentUtil.fireEvent(UI.getCurrent(), new UpdateContractFinancialBalance(this));
                Notification.show("Vícenáklad uložen").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (ValidationException _) {
                Notification.show("Nevalidní formulář").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    private void setupGridPro() {
        gridPro.setEditOnClick(true);
        gridPro.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_READ_ONLY_CELLS);

        gridPro.addEditColumn(
                        ExtraCost::getExtraCost,
                        new TextRenderer<>(extraCost -> Math.round(extraCost.getExtraCost()) + "Kč")
                )
                .custom(new PriceField(null), (extraCost, newValue) -> {
                    if (newValue.isNaN() || newValue < 0 || newValue > Double.MAX_VALUE)
                        CustomNotification.showSimpleError("Nevalidní vícenáklad.");
                    else
                        extraCost.setExtraCost(newValue);
                })
                .setHeader("Vícenáklad")
                .setKey(ExtraCost_.EXTRA_COST)
                .setSortable(true);
        gridPro.addEditColumn(ExtraCost::getDescription)
                .text((extraCost, newValue) -> {
                    if (newValue.isEmpty())
                        CustomNotification.showSimpleError("Popis je povinný");
                    else
                        extraCost.setDescription(newValue);
                })
                .setHeader("Popis vícenákladu")
                .setKey(ExtraCost_.DESCRIPTION)
                .setSortable(true);
        gridPro.addColumn(extraCost -> extraCost.getUpdatedBy().getFullName())
                .setHeader("Upraveno")
                .setKey(ExtraCost_.UPDATED_BY);
        gridPro.addColumn(new LocalDateTimeRenderer<>(ExtraCost::getUpdated, "H:mm d. M. yyyy"))
                .setHeader("Upraveno")
                .setKey(ExtraCost_.UPDATED)
                .setSortable(true);

        gridPro.setMultiSort(true, Grid.MultiSortPriority.APPEND);

        dataProvider = new CallbackDataProvider<>(
                query -> {
                    if (contract != null)
                        return extraCostService.fetch(query, contract.getId());
                    else
                        return Stream.empty();
                },
                query -> {
                    if (contract != null)
                        return extraCostService.size(query, contract.getId());
                    else
                        return 0;
                }
        );
        dataProvider.addDataProviderListener(_ -> ComponentUtil.fireEvent(UI.getCurrent(), new UpdateContractFinancialBalance(this)));

        gridPro.setDataProvider(dataProvider);

        gridPro.addItemPropertyChangedListener(event -> {
            extraCostService.save(event.getItem());
            dataProvider.refreshAll();
        });

        addContextMenu();
    }

    private void addContextMenu() {
        GridContextMenu<ExtraCost> contextMenu = new GridContextMenu<>(gridPro);
        GridMenuItem<ExtraCost> delete = contextMenu.addItem("Smazat", event -> {
            event.getItem().ifPresent(extraCostService::delete);
            dataProvider.refreshAll();
        });
        delete.addComponentAsFirst(CustomComponent.createContextIcon(VaadinIcon.TRASH));

        Tooltip.forComponent(gridPro)
                .withText("Kontextové menu na pravém tlačítku myši.");
    }
    //endregion

    public void setContract(Contract contract) {
        this.contract = contract;
        dataProvider.refreshAll();
    }
}
