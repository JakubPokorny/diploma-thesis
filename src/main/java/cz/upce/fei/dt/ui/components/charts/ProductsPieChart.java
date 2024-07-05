package cz.upce.fei.dt.ui.components.charts;

import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.upce.fei.dt.beckend.dto.IMostSaleableProducts;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.beckend.services.filters.DashboardFilter;
import cz.upce.fei.dt.beckend.utilities.CzechI18n;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class ProductsPieChart extends Row {
    private final ProductService productService;

    private final Chart chart = new Chart(ChartType.PIE);
    private final Configuration config = chart.getConfiguration();
    private final Tooltip tooltip = new Tooltip();
    private final DataSeries series = new DataSeries();
    private final PlotOptionsPie options = new PlotOptionsPie();

    private final FormLayout filters = new FormLayout();
    private final DatePicker fromField = new DatePicker("Od");
    private final DatePicker toField = new DatePicker("Do");
    private final MultiSelectComboBox<Product> productsMSB = new MultiSelectComboBox<>("Zahrnout produkty");
    private final MultiSelectComboBox<Status.Theme> themesMSB = new MultiSelectComboBox<>("Zahrnout statusy", Status.Theme.values());
    private final Binder<DashboardFilter> binder = new BeanValidationBinder<>(DashboardFilter.class);

    public ProductsPieChart(ProductService productService) {
        this.productService = productService;

        setupPieChart();
        setupPieChartFilters();

        this.add(chart, 2);
        this.add(filters);

        updateChart();
    }

    public void updateChart() {
        try {
            DashboardFilter dashboardFilter = new DashboardFilter();
            binder.writeBean(dashboardFilter);

            series.clear();
            for (IMostSaleableProducts product : productService.getMostSaleableProducts(dashboardFilter)) {
                series.add(new DataSeriesItem(product.getName(), product.getAmount()));
            }
            config.setSeries(series);
            chart.setConfiguration(config);
        } catch (ValidationException exp) {
            Notification.show("Neuplný výběr.").addThemeVariants(NotificationVariant.LUMO_WARNING);
        }
    }

    private void setupPieChart() {
        chart.setClassName("board-cell");

        config.setTooltip(tooltip);

        options.setAllowPointSelect(true);
        options.setCursor(Cursor.POINTER);
        options.setShowInLegend(true);
        series.setPlotOptions(options);

        chart.setConfiguration(config);
        chart.setVisibilityTogglingDisabled(true);
    }

    private void setupPieChartFilters() {
        filters.addClassNames("board-cell", "chart-filters");

        filters.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        binder.forField(fromField)
                .asRequired()
                .bind(DashboardFilter::getFromDate, DashboardFilter::setFromDateTime);
        binder.forField(toField)
                .asRequired()
                .bind(DashboardFilter::getToDate, DashboardFilter::setToDateTime);
        binder.forField(productsMSB)
                .asRequired()
                .bind(DashboardFilter::getProductsFilter, DashboardFilter::setProductsFilter);
        binder.forField(themesMSB)
                .asRequired()
                .bind(DashboardFilter::getStatusFilter, DashboardFilter::setStatusFilter);

        fromField.setI18n(CzechI18n.getDatePickerI18n());
        fromField.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        fromField.addValueChangeListener(_ -> updateChart());

        toField.setI18n(CzechI18n.getDatePickerI18n());
        toField.setValue(LocalDate.now());
        toField.addValueChangeListener(_ -> updateChart());

        themesMSB.setItemLabelGenerator(theme -> String.format("%s, %s", theme, theme.getMeaning()));
        themesMSB.setClearButtonVisible(true);
        themesMSB.select(Status.Theme.SUCCESS, Status.Theme.PENDING, Status.Theme.WARNING, Status.Theme.CONTRAST);
        themesMSB.addSelectionListener(_ -> updateChart());

        productsMSB.setItemLabelGenerator(Product::getName);
        productsMSB.setClearButtonVisible(true);
        var products = productsMSB.setItems(productService::findAllByName);
        productsMSB.select(products.getItems().collect(Collectors.toSet()));
        productsMSB.addSelectionListener(_ -> updateChart());

        filters.add(fromField, toField, productsMSB, themesMSB);
    }
}
