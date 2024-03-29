package cz.upce.fei.dt.beckend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum State {
    CREATED("Vytvořeno"),
    PRODUCTION("V produkci"),
    PACKAGING("Balení"),
    SENT("Zasnálo"),
    INVOICED("Fakturováno"),
    DONE("Hotovo"),
    CANCELLED("Zrušeno");

    private final String name;

}
