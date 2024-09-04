/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/ProjetoIC/src/Models/dnl/SistemaQ.dnl
-1847762482
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.Serializable;

// Custom library code
//ID:LIB:0
import java.lang.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;

import com.ms4systems.devs.core.message.Message;
import com.ms4systems.devs.core.message.MessageBag;
import com.ms4systems.devs.core.message.Port;
import com.ms4systems.devs.core.message.impl.MessageBagImpl;
import com.ms4systems.devs.core.model.impl.AtomicModelImpl;
import com.ms4systems.devs.core.simulation.Simulation;
import com.ms4systems.devs.core.simulation.Simulator;
import com.ms4systems.devs.extensions.PhaseBased;
import com.ms4systems.devs.extensions.StateVariableBased;
import com.ms4systems.devs.helpers.impl.SimulationOptionsImpl;
import com.ms4systems.devs.simviewer.standalone.SimViewer;

//ENDID
// End custom library code
@SuppressWarnings("unused")
public class SistemaQ extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_CONTADORQ = 0;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Contador ContadorQ;

    //ENDID
    String phase = "Receber";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<Contador> inContador =
        addInputPort("inContador", Contador.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<Contador> outContador =
        addOutputPort("outContador", Contador.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public SistemaQ() {
        this("SistemaQ");
    }

    public SistemaQ(String name) {
        this(name, null);
    }

    public SistemaQ(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        passivateIn("Receber");

        // Initialize Variables
        //ID:INIT
        ContadorQ = new Contador(8);

        //ENDID
        // End initialize variables
    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("Enviar")) {
            getSimulator().modelMessage("Internal transition from Enviar");

            //ID:TRA:Enviar
            holdIn("Esperar", 0.0);
            this.ContadorQ.setValue(ContadorQ.getValue() + 8);

            //ENDID
            return;
        }
        if (phaseIs("Esperar")) {
            getSimulator().modelMessage("Internal transition from Esperar");

            //ID:TRA:Esperar
            passivateIn("Receber");
            //ENDID
            // Internal event code
            //ID:INT:Esperar
            this.ContadorQ.setValue(ContadorQ.getValue() + 8);

            //ENDID
            // End internal event code
            return;
        }

        //passivate();
    }

    @Override
    public void externalTransition(double timeElapsed, MessageBag input) {
        currentTime += timeElapsed;
        // Subtract time remaining until next internal transition (no effect if sigma == Infinity)
        sigma -= timeElapsed;

        // Store prior data
        previousPhase = phase;
        previousSigma = sigma;

        // Fire state transition functions
        if (phaseIs("Receber")) {
            if (input.hasMessages(inContador)) {
                ArrayList<Message<Contador>> messageList =
                    inContador.getMessages(input);

                holdIn("Enviar", 0.0);

            	Contador c = (Contador) messageList.get(0).getData();
                ContadorQ.setValue((Math.max(c.getValue(), this.ContadorQ.getValue()) + 1));
                this.ContadorQ.setValue(ContadorQ.getValue() + 8);
                return;
            }
        }
    }

    @Override
    public void confluentTransition(MessageBag input) {
        // confluentTransition with internalTransition first (by default)
        internalTransition();
        externalTransition(0, input);
    }

    @Override
    public Double getTimeAdvance() {
        return sigma;
    }

    @Override
    public MessageBag getOutput() {
        MessageBag output = new MessageBagImpl();

        if (phaseIs("Enviar")) {
            // Output event code
            //ID:OUT:Enviar
            output.add(outContador, ContadorQ);

            //ENDID
            // End output event code
        }
        return output;
    }

    // Custom function definitions

    // End custom function definitions
    public static void main(String[] args) {
        SimulationOptionsImpl options = new SimulationOptionsImpl(args, true);

        // Uncomment the following line to disable SimViewer for this model
        // options.setDisableViewer(true);

        // Uncomment the following line to disable plotting for this model
        // options.setDisablePlotting(true);

        // Uncomment the following line to disable logging for this model
        // options.setDisableLogging(true);
        SistemaQ model = new SistemaQ();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new com.ms4systems.devs.core.simulation.impl.SimulationImpl("SistemaQ Simulation",
                    model, options);
            sim.startSimulation(0);
            sim.simulateIterations(Long.MAX_VALUE);
        } else { // Use SimViewer
            SimViewer viewer = new SimViewer();
            viewer.open(model, options);
        }
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    // Getter/setter for ContadorQ
    public void setContadorQ(Contador ContadorQ) {
        propertyChangeSupport.firePropertyChange("ContadorQ", this.ContadorQ,
            this.ContadorQ = ContadorQ);
    }

    public Contador getContadorQ() {
        return this.ContadorQ;
    }

    // End getter/setter for ContadorQ

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "ContadorQ" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { ContadorQ };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Contador.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_CONTADORQ:
                setContadorQ((Contador) value);
                return;

            default:
                return;
        }
    }

    // Convenience functions
    protected void passivate() {
        passivateIn("passive");
    }

    protected void passivateIn(String phase) {
        holdIn(phase, Double.POSITIVE_INFINITY);
    }

    protected void holdIn(String phase, Double sigma) {
        this.phase = phase;
        this.sigma = sigma;
        getSimulator()
            .modelMessage("Holding in phase " + phase + " for time " + sigma);
    }

    protected static File getModelsDirectory() {
        URI dirUri;
        File dir;
        try {
            dirUri = SistemaQ.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                SistemaQ.class.getResource(".").toString());
        }
        boolean foundModels = false;
        while (dir != null && dir.getParentFile() != null) {
            if (dir.getName().equalsIgnoreCase("java") &&
                  dir.getParentFile().getName().equalsIgnoreCase("models")) {
                return dir.getParentFile();
            }
            dir = dir.getParentFile();
        }
        throw new RuntimeException(
            "Could not find Models directory from model path: " +
            dirUri.toASCIIString());
    }

    protected static File getDataFile(String fileName) {
        return getDataFile(fileName, "txt");
    }

    protected static File getDataFile(String fileName, String directoryName) {
        File modelDir = getModelsDirectory();
        File dir = new File(modelDir, directoryName);
        if (dir == null) {
            throw new RuntimeException("Could not find '" + directoryName +
                "' directory from model path: " + modelDir.getAbsolutePath());
        }
        File dataFile = new File(dir, fileName);
        if (dataFile == null) {
            throw new RuntimeException("Could not find '" + fileName +
                "' file in directory: " + dir.getAbsolutePath());
        }
        return dataFile;
    }

    protected void msg(String msg) {
        getSimulator().modelMessage(msg);
    }

    // Phase display
    public boolean phaseIs(String phase) {
        return this.phase.equals(phase);
    }

    public String getPhase() {
        return phase;
    }

    public String[] getPhaseNames() {
        return new String[] { "Receber", "Enviar", "Esperar" };
    }
}
