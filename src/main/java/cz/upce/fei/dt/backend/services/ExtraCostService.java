package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.backend.dto.IExtraCost;
import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.ExtraCost;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.exceptions.AuthenticationException;
import cz.upce.fei.dt.backend.repositories.ExtraCostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ExtraCostService {
    private final ExtraCostRepository extraCostRepository;
    private final AuthenticationContext authenticationContext;

    public int size(Query<ExtraCost, Void> query, Long contractId) {
        return (int) fetch(query, contractId).count();
    }

    public Stream<ExtraCost> fetch(Query<ExtraCost, Void> query, Long contractId) {
        if (contractId == null)
            return Stream.empty();

        PageRequest pageRequest = VaadinSpringDataHelpers.toSpringPageRequest(query);
        return extraCostRepository.findAllByContractId(pageRequest, contractId).stream();
    }

    public void save(ExtraCost extraCost) throws AuthenticationException {
        User user = authenticationContext.getAuthenticatedUser(User.class)
                .orElseThrow(() -> new AuthenticationException("Neznámý uživatel. Přihlašte se prosím."));

        if (extraCost.getId() == null)
            extraCost.setCreatedBy(user);

        extraCost.setUpdatedBy(user);
        extraCostRepository.save(extraCost);
    }

    public Stream<ExtraCost> findAll(Query<ExtraCost, Void> query) {
        return extraCostRepository.findAll(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream();
    }

    public void delete(ExtraCost extraCost) {
        extraCostRepository.delete(extraCost);
    }

    public IExtraCost countByContractId(Contract contract) {
        if (contract == null || contract.getId() == null)
            return null;
        IExtraCost iExtraCost = extraCostRepository.countByContractId(contract.getId());
        return iExtraCost.getCount() == 0 ? null : iExtraCost;
    }
}
