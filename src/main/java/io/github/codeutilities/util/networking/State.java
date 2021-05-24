package io.github.codeutilities.util.networking;

import io.github.codeutilities.events.interfaces.HyperCubeEvents;
import io.github.codeutilities.events.register.ReceiveChatMessageEvent;
import io.github.codeutilities.features.social.tab.Client;
import io.github.codeutilities.util.file.ILoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.java_websocket.enums.ReadyState;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class State {

    private final HyperCubeEvents invoker = HyperCubeEvents.CHANGE_STATE.invoker();
    private static final String EMPTY = "                                       ";
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public Plot plot;
    public Mode mode;
    public Node node;
    public boolean session;

    public State() {
        this.mode = null;
        this.node = null;
        this.plot = null;
        this.session = false;
    }

    public State(State state) {
        this.mode = state.getMode();
        this.node = state.getNode();
        this.plot = state.getPlot();
        this.session = state.isInSession();
    }

    public State(Mode mode, Node node, Plot plot, boolean session) {
        this.mode = mode;
        this.node = node;
        this.plot = plot;
        this.session = session;
        invoker.update(this, this);
    }

    public State(String modeid, String nodeid, Plot plot, boolean session) {
        this.mode = Mode.getByIdentifier(modeid);
        this.node = Node.getByIdentifier(nodeid);
        this.plot = plot;
        this.session = session;
        invoker.update(this, this);
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
            if(this == null) return null;
            return identifier;
        }

        public String getContinuousVerb() {
            if(this == null) return null;
            return continuousVerb;
        }

        public static Mode getByIdentifier(String identifier) {
            for(Mode mode : values()) {
                if(mode.getIdentifier().equalsIgnoreCase(identifier)) {
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
            if(this == null) return null;
            return identifier;
        }

        public static Node getByIdentifier(String identifier) {
            for(Node node : values()) {
                if(node.getIdentifier().equalsIgnoreCase(identifier)) {
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
        private final String id;
        private final String status;

        public Plot(String name, String id, String status){
            this.name = name;
            this.id = id;
            this.status = status;
        }

        public String getId() {
            if(this == null) return null;
            return id;
        }

        public String getName() {
            if(this == null) return null;
            return name;
        }

        public String getStatus() {
            if(this == null) return null;
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
        if(this == null) return null;
        return mode;
    }

    public Node getNode() {
        if(this == null) return null;
        return node;
    }

    public Plot getPlot() {
        if(this == null) return null;
        return plot;
    }

    public boolean isInSession() {
        if(this == null) return false;
        return session;
    }

    public void setInSession(boolean session) {
        State old = this.copy();
        boolean update = false;
        if(this.session != session) update = true;
        this.session = session;
        if(update) invoker.update(this, old);
    }

    public void setMode(Mode mode) {
        if(mode == Mode.SPAWN) this.setPlot(null);
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
        State state = (State) o;
        return session == state.session &&
                Objects.equals(plot, state.plot) &&
                mode == state.mode &&
                node == state.node;
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoker, plot, mode, node);
    }

    public State copy() {
        return new State(this);
    }
    
    public static State fromLocate(Text msg) {
        String text = msg.getString().replaceAll("§.", "");
        State finalstate = new State();

        if (text.startsWith(EMPTY + "\nYou are currently at spawn\n")) {
            finalstate.setMode(Mode.SPAWN);
            Node node = Node.getByIdentifier(text.replaceAll("\n", "").replaceFirst(" {39}You are currently at spawn", "")
                    .replaceFirst("→ Server: Node ", "")
                    .replaceFirst(" {39}", ""));
            finalstate.setNode(node);
        } else {
            // PLOT ID
            Pattern pattern = Pattern.compile("\\[[0-9]+]\n");
            Matcher matcher = pattern.matcher(text);
            String id = "";
            while (matcher.find()) {
                id = matcher.group();
            }
            id = id.replaceAll("[\\[\\]\n]", "");

            // PLOT NODE
            pattern = Pattern.compile("Node ([0-9]|Beta)\n");
            matcher = pattern.matcher(text);
            String node = "";
            while (matcher.find()) {
                node = matcher.group();
            }

            // PLOT NAME
            pattern = Pattern.compile("\n\n→ .+ \\[[0-9]+]\n");
            matcher = pattern.matcher(text);
            String name = "";
            while (matcher.find()) {
                name = matcher.group();
            }
            name = name.replaceAll("(^\n\n→ )|( \\[[0-9]+]\n$)", "");

            // CUSTOM STATUS
            String customStatus = "";
            if (DFInfo.currentState.getMode() == State.Mode.PLAY) {
                pattern = Pattern.compile("\n→ ");
                matcher = pattern.matcher(text);
                int headerAmt = 0;
                while (matcher.find()) headerAmt++;
                if (headerAmt == 4) {
                    customStatus = text
                            .replaceFirst("^.*\n.*\n\n→ .*\n→ ", "");
                    pattern = Pattern.compile("^.*");
                    matcher = pattern.matcher(customStatus);
                    while (matcher.find()) {
                        customStatus = matcher.group();
                    }
                }
            }

            node = node.replaceFirst("Node ", "").replaceAll("\n", "");
            finalstate.setNode(State.Node.getByIdentifier(node));
            finalstate.setPlot(new State.Plot(name, id, customStatus));

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

    public String toJson() {
        return this == null ? "null" : "{\"mode\":"+(this.getMode() == null ? "null" : ("{\"identifier\":\""+this.getMode().getIdentifier()+"\",\"verb\":\""+this.getMode().getContinuousVerb()+"\"}"))+",\"node\":"+(this.getNode() == null ? "null" : ("{\"identifier\":\""+this.getNode().getIdentifier()+"\"}"))+",\"plot\":"+ (this.getPlot() == null ? "null" : ("{\"id\":\""+this.getPlot().getId()+"\",\"name\":\""+this.getPlot().getName()+"\",\"status\":\""+this.getPlot().getStatus()+"\"}"))+"}";
    }

    public void sendLocate() {
        if(mc.player != null){
            if(!mc.player.isDead()){
                ReceiveChatMessageEvent.locating += 1;
                mc.player.sendChatMessage("/locate");
            }
        }
    }

    public static class Locater implements ILoader {

        @Override
        public void load() {
            Thread thread = new Thread(() -> {
                while (true) {
                    if (DFInfo.isOnDF() && mc.player != null) {
                        DFInfo.currentState.sendLocate();
                    }
                    if (!Client.client.isOpen() && !(Client.client.getReadyState() == ReadyState.NOT_YET_CONNECTED)) Client.connect();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            Executors.newSingleThreadExecutor().submit(thread);

        }
    }

    public static class CurrentState extends State {

        private final HyperCubeEvents invoker = HyperCubeEvents.CHANGE_STATE.invoker();

        public CurrentState() {
            super();
        }

        public CurrentState(State state) {
            super(state);
        }

        public CurrentState(Mode mode, Node node, Plot plot, boolean session) {
            super(mode, node, plot, session);
        }

        public CurrentState(String modeid, String nodeid, Plot plot, boolean session) {
            super(modeid, nodeid, plot, session);
        }

        @Override
        public void setInSession(boolean session) {
            State old = this.copy();
            boolean update = false;
            if(this.session != session) update = true;
            this.session = session;
            if(update) invoker.update(this, old);
        }

        @Override
        public void setMode(Mode mode) {
            State old = this.copy();
            boolean update = false;
            if(this.mode != mode) update = true;
            if(mode == Mode.SPAWN || mode == Mode.OFFLINE) this.plot = null;
            if(mode == Mode.OFFLINE) this.node = null;
            this.mode = mode;
            if(update) invoker.update(this, old);
        }

        @Override
        public void setNode(Node node) {
            State old = this.copy();
            boolean update = false;
            if(this.node != node) update = true;
            this.node = node;
            if(update) invoker.update(this, old);
        }

        @Override
        public void setPlot(Plot plot) {
            State old = this.copy();
            boolean update = false;
            if(this.plot != plot) update = true;
            this.plot = plot;
            if(update) invoker.update(this, old);
        }
    }
}