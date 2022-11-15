package io.github.homchom.recode.sys.networking;

import com.google.gson.*;
import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.server.*;
import io.github.homchom.recode.sys.player.chat.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.regex.*;

@Deprecated
public class LegacyState {
    private static final String EMPTY = "                                       ";
    private static final Minecraft mc = Minecraft.getInstance();

    public Plot plot;
    public Mode mode;
    public Node node;
    public boolean session;

    public LegacyState() {
        this.mode = null;
        this.node = null;
        this.plot = null;
        this.session = false;
    }

    public LegacyState(LegacyState state) {
        this.mode = state.getMode();
        this.node = state.getNode();
        this.plot = state.getPlot();
        this.session = state.isInSession();
    }

    public LegacyState(Mode mode, Node node, Plot plot, boolean session) {
        this.mode = mode;
        this.node = node;
        this.plot = plot;
        this.session = session;
        notifyStateChange(this, this);
    }

    public LegacyState(String modeid, String nodeid, Plot plot, boolean session) {
        this.mode = Mode.getByIdentifier(modeid);
        this.node = Node.getByIdentifier(nodeid);
        this.plot = plot;
        this.session = session;
        notifyStateChange(this, this);
    }

    public enum Mode {
        OFFLINE("Offline", "Offline"),
        SPAWN("Spawn", "Idle"),
        PLAY("Play", "Playing"),
        BUILD("Build", "Building"),
        DEV("Dev", "Coding");

        private final String identifier;
        private final String continuousVerb;

        Mode(String identifier, String continuousVerb) {
            this.identifier = identifier;
            this.continuousVerb = continuousVerb;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getContinuousVerb() {
            return continuousVerb;
        }

        public static Mode getByIdentifier(String identifier) {
            for(Mode mode : values()) {
                if (mode.getIdentifier().equalsIgnoreCase(identifier)) {
                    return mode;
                }
            }
            return OFFLINE;
        }

        @Override
        public String toString() {
            return "Mode{" +
                    "identifier='" + identifier + '\'' +
                    ", continuousVerb='" + continuousVerb + '\'' +
                    '}';
        }
    }

    public enum Node {
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        BETA("Beta"),
        DEV("Dev"),
        DEV2("Dev2"),

        UNKNOWN("?");

        private final String identifier;

        Node(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public static Node getByIdentifier(String identifier) {
            for(Node node : values()) {
                if (node.getIdentifier().equals(identifier)) {
                    return node;
                }
            }
            return UNKNOWN;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "identifier='" + identifier + '\'' +
                    '}';
        }
    }

    public static class Plot {
        private final String name;
        private final String owner;
        private final String id;
        private final String status;

        public Plot(String name, String owner, String id, String status){
            this.name = name;
            this.owner = owner;
            this.id = id;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }

        public String getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "Plot{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Plot plot = (Plot) o;
            return Objects.equals(name, plot.name) &&
                    Objects.equals(id, plot.id) &&
                    Objects.equals(status, plot.status);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, id, status);
        }
    }

    public Mode getMode() {
        return mode;
    }

    public Node getNode() {
        return node;
    }

    public Plot getPlot() {
        return plot;
    }

    public boolean isInSession() {
        return session;
    }

    public void setInSession(boolean session) {
        LegacyState old = this.copy();
        boolean update = this.session != session;
        this.session = session;
        if (update) notifyStateChange(this, old);
    }

    public void setMode(Mode mode) {
        if (mode == Mode.SPAWN) this.setPlot(null);
        this.mode = mode;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }

    @Override
    public String toString() {
        return "State{" +
                "plot=" + plot +
                ", mode=" + mode +
                ", node=" + node +
                ", session=" + session +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegacyState state = (LegacyState) o;
        return session == state.session &&
                Objects.equals(plot, state.plot) &&
                mode == state.mode &&
                node == state.node;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plot, mode, node);
    }

    public LegacyState copy() {
        return new LegacyState(this);
    }

    public static LegacyState fromLocate(Message message, LegacyState stateSource) {
        Component msg = message.getText();

        String text = msg.getString().replaceAll("§.", "");
        LegacyState finalstate = stateSource.copy();

        if (text.startsWith(EMPTY + "\nYou are currently at spawn\n")) {
            finalstate.setMode(Mode.SPAWN);
            Node node = Node.getByIdentifier(text.replaceAll("\n", "").replaceFirst(" {39}You are currently at spawn", "")
                    .replaceFirst("→ Server: Node ", "")
                    .replaceFirst(" {39}", ""));
            finalstate.setNode(node);
        } else {
            // PLOT ID
            Pattern pattern = Pattern.compile("\\[([0-9]+)]\n");
            Matcher matcher = pattern.matcher(text);
            String id = "";
            if (matcher.find()) {
                id = matcher.group(1);
            }

            // PLOT NODE
            pattern = Pattern.compile("Node ([0-9]|Beta)\n");
            matcher = pattern.matcher(text);
            String node = "";
            if (matcher.find()) {
                node = matcher.group(1);
            }

            // PLOT NAME
            pattern = Pattern.compile("\n\n→ (.+) \\[[0-9]+]\n");
            matcher = pattern.matcher(text);
            String name = "";
            if (matcher.find()) {
                name = matcher.group(1);
            }

            // PLOT OWNER
            pattern = Pattern.compile("\\n→ Owner: ((?:(?! ).)*)");
            matcher = pattern.matcher(text);
            String owner = "";
            if (matcher.find()) {
                owner = matcher.group(1);
            }

            // CUSTOM STATUS
            String[] lines = text.split("\\r?\\n");
            String customStatus = "";
            if (!lines[4].contains("Owner: ")) {
                pattern = Pattern.compile("→ (.+)");
                matcher = pattern.matcher(lines[4]);
                if (matcher.find()) {
                    customStatus = matcher.group(1);
                }
            }

            finalstate.setNode(LegacyState.Node.getByIdentifier(node));
            finalstate.setPlot(new LegacyState.Plot(name, owner, id, customStatus));

            if (text.startsWith(EMPTY + "\nYou are currently playing on:")) {
                finalstate.setMode(Mode.PLAY);
            } else if (text.startsWith(EMPTY + "\nYou are currently building on:")) {
                finalstate.setMode(Mode.BUILD);
            } else if (text.startsWith(EMPTY + "\nYou are currently coding on:")) {
                finalstate.setMode(Mode.DEV);
            } else {
                finalstate.setMode(Mode.OFFLINE);
            }

        }

        return finalstate;
    }

    public JsonElement toJson() {
        return JsonParser.parseString("{\"mode\":"+(this.getMode() == null ? "null" : ("{\"identifier\":\""+this.getMode().getIdentifier()+"\",\"verb\":\""+this.getMode().getContinuousVerb()+"\"}"))+",\"node\":"+(this.getNode() == null ? "null" : ("{\"identifier\":\""+this.getNode().getIdentifier()+"\"}"))+",\"plot\":"+ (this.getPlot() == null ? "null" : ("{\"id\":\""+this.getPlot().getId()+"\",\"name\":\""+this.getPlot().getName()+"\",\"status\":\""+this.getPlot().getStatus()+"\"}"))+"}");
    }

    public void sendLocate() {
        if (mc.player != null){
            if (!mc.player.isDeadOrDying()){
                ChatUtil.executeCommand("locate");
                MessageGrabber.hide(1, MessageType.LOCATE);
            }
        }
    }

    private static void notifyStateChange(LegacyState newState, LegacyState oldState) {
        ChangeDFStateEvent.INSTANCE.run(new StateChange(newState, oldState));
    }

    public static class CurrentState extends LegacyState {
        public CurrentState() {
            super();
        }

        public CurrentState(LegacyState state) {
            super(state);
        }

        public CurrentState(Mode mode, Node node, Plot plot, boolean session) {
            super(mode, node, plot, session);
        }

        public CurrentState(String modeId, String nodeId, Plot plot, boolean session) {
            super(modeId, nodeId, plot, session);
        }

        @Override
        public void setInSession(boolean session) {
            LegacyState old = this.copy();
            boolean update = this.session != session;
            this.session = session;
            if (update) notifyStateChange(this, old);
        }

        @Override
        public void setMode(Mode mode) {
            LegacyState old = this.copy();
            boolean update = this.mode != mode;
            if (mode == Mode.SPAWN || mode == Mode.OFFLINE) this.plot = null;
            if (mode == Mode.OFFLINE) this.node = null;
            this.mode = mode;
            if (update) notifyStateChange(this, old);
        }

        @Override
        public void setNode(Node node) {
            LegacyState old = this.copy();
            boolean update = this.node != node;
            this.node = node;
            if (update) notifyStateChange(this, old);
        }

        @Override
        public void setPlot(Plot plot) {
            LegacyState old = this.copy();
            boolean update = this.plot != plot;
            this.plot = plot;
            if (update) notifyStateChange(this, old);
        }
    }
}