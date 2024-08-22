package cz.upce.fei.dt.backend.services.mappers;

import cz.upce.fei.dt.backend.dto.ICheckExpiredFinalDeadline;
import cz.upce.fei.dt.backend.dto.ICheckExpiredPartialDeadline;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailMapper {
    public Map<String, List<String>> mapICheckExpiredFinalDeadline(List<ICheckExpiredFinalDeadline> projections) {
        Map<String, List<String>> map = new HashMap<>();
        projections.forEach(projection -> {
            String email = projection.getEmail();

            String detail = String.format("\t%s; zakázka %s; v ceně %skč; s produkty: %s",
                    projection.getFinalDeadline().format(DateTimeFormatter.ofPattern("d. M. yyyy")),
                    projection.getId(),
                    Math.round(projection.getPrice()),
                    projection.getOrderedProducts());

            if (!map.containsKey(email)) {
                ArrayList<String> details = new ArrayList<>();
                details.add(detail);
                map.put(email, details);
            } else {
                map.get(email).add(detail);
            }
        });
        return map;
    }

    public Map<String, List<String>> mapICheckExpiredPartialDeadline(List<ICheckExpiredPartialDeadline> projections) {
        Map<String, List<String>> map = new HashMap<>();
        projections.forEach(projection -> {
            String email = projection.getEmail();

            String detail = String.format("\t%s; zakázka %s v ceně %skč; s produkty: %s; ve stavu: %s",
                    projection.getPartialDeadline().format(DateTimeFormatter.ofPattern("d. M. yyyy")),
                    projection.getId(),
                    Math.round(projection.getPrice()),
                    projection.getOrderedProducts(),
                    projection.getStatus());

            if (!map.containsKey(email)) {
                ArrayList<String> details = new ArrayList<>();
                details.add(detail);
                map.put(email, details);
            } else {
                map.get(email).add(detail);
            }
        });
        return map;
    }
}
