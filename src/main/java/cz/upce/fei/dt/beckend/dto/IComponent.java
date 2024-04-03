package cz.upce.fei.dt.beckend.dto;

public interface IComponent {
    Long getId();
    String getName();
    int getAmount();
    int getMin();
    IUser getIUser();
}
