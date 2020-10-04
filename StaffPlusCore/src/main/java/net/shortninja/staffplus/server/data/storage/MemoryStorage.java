package net.shortninja.staffplus.server.data.storage;

import net.shortninja.staffplus.player.User;
import net.shortninja.staffplus.player.attribute.Ticket;
import net.shortninja.staffplus.unordered.IReport;
import net.shortninja.staffplus.unordered.IWarning;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public final class MemoryStorage implements IStorage {

    private final Map<UUID, DataHolder> data = new HashMap<>();

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public byte[] getPassword(Player player) {
        return new byte[0]; // Due to insecurity in the backend, let it gracefully pass.
    }

    @Override
    public void setPassword(Player player, byte[] password) {
        // Due to insecurity in the backend, let it gracefully pass.
    }

    @Override
    public short getGlassColor(User user) {
        return getOrPut(user.getUuid()).glassColor;
    }

    @Override
    public void setGlassColor(User user, short glassColor) {
        getOrPut(user.getUuid()).glassColor = glassColor;
    }

    @Override
    public List<IReport> getReports(UUID uuid) {
        return getOrPut(uuid).reports;
    }

    @Override
    public List<IWarning> getWarnings(UUID uuid) {
        return getOrPut(uuid).warnings;
    }

    @Override
    public void addReport(IReport report) {
        getOrPut(report.getUuid()).reports.add(report);
    }

    @Override
    public void addWarning(IWarning warning) {
        getOrPut(warning.getUuid()).warnings.add(warning);
    }

    @Override
    public void removeReport(User user) {
        getOrPut(user.getUuid()).reports.removeIf(r -> r.getUuid().equals(user.getUuid()));
    }

    @Override
    public void removeWarning(UUID uuid) {
        getOrPut(uuid).warnings.removeIf(r -> r.getUuid().equals(uuid));
    }

    @Override
    public Set<Ticket> getTickets() {
        final Set<Ticket> tickets = new HashSet<>();

        for (List<Ticket> cTickets : data.values().stream().map(dataHolder -> dataHolder.tickets).collect(Collectors.toSet())) {
            tickets.addAll(cTickets);
        }

        return tickets;
    }

    @Override
    public Ticket getTicketByUUID(UUID uuid) {
        return getOrPut(uuid).tickets.stream().filter(t -> t.getUuid().equals(uuid)).findAny().orElse(null);
    }

    @Override
    public Ticket getTickById(int id) {
        return getTickets().stream().filter(t -> t.getId() == id).findAny().orElse(null);
    }

    @Override
    public void addTicket(Ticket ticket) {
        getOrPut(ticket.getUuid()).tickets.add(ticket);
    }

    @Override
    public void removeTicket(Ticket ticket) {
        getOrPut(ticket.getUuid()).tickets.remove(ticket);
    }

    private DataHolder getOrPut(UUID id) {
        if (data.containsKey(id)) {
            return data.get(id);
        } else {
            final DataHolder holder = new DataHolder();
            data.putIfAbsent(id, holder);

            return holder;
        }
    }

    private static class DataHolder {

        private final List<Ticket> tickets = new ArrayList<>();
        private final List<IReport> reports = new ArrayList<>();
        private final List<IWarning> warnings = new ArrayList<>();
        private short glassColor;
    }
}
