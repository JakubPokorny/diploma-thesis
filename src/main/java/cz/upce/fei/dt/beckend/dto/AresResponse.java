package cz.upce.fei.dt.beckend.dto;

import cz.upce.fei.dt.beckend.entities.Contact;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AresResponse {
    String ico;
    String dic;
    String obchodniJmeno;
    Sidlo sidlo;

    @Getter
    @Setter
    public static class Sidlo{
        String nazevUlice;
        int cisloDomovni;
        int psc;
        String nazevObce;
        String nazevStatu;
    }

    @Override
    public String toString() {
        return obchodniJmeno +", "
                + sidlo.nazevUlice + " " + sidlo.cisloDomovni + ", " + sidlo.nazevObce + " " + sidlo.psc;
    }

    public boolean equals(Contact contact) {
        return ico.equals(contact.getICO());
    }
}
