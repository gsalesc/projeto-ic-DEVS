/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/ProjetoIC/src/Models/dnl/SistemaR.dnl
-2019651569
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
public class SistemaR extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_CONTADORR = 0;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected Contador ContadorR;

    //ENDID
    String phase = "Esperar";
    String previousPhase = null;
    Double sigma = 0.0;
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

    public SistemaR() {
        this("SistemaR");
    }

    public SistemaR(String name) {
        this(name, null);
    }

    public SistemaR(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        holdIn("Esperar", 0.0);

        // Initialize Variables
        //ID:INIT
        ContadorR = new Contador(10);

        //ENDID
        // End initialize variables
    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("Esperar")) {
            getSimulator().modelMessage("Internal transition from Esperar");

            //ID:TRA:Esperar
            passivateIn("Receber");
            //ENDID
            // Internal event code
            //ID:INT:Esperar
            this.ContadorR.setValue(ContadorR.getValue() + 10);

            //ENDID
            // End internal event code
            return;
        }
        if (phaseIs("Enviar")) {
            getSimulator().modelMessage("Internal transition from Enviar");

            //ID:TRA:Enviar
            holdIn("Esperar", 0.0);

            //ENDID
            // Internal event code
            this.ContadorR.setValue(ContadorR.getValue() + 10);

            //ID:INT:Enviar


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

            	Contador c = (Contador) messageList.get(0).getData();
                ContadorR.setValue((Math.max(c.getValue(), this.ContadorR.getValue()) + 1));
                this.ContadorR.setValue(ContadorR.getValue() + 10);
                
                holdIn("Enviar", 0.0);

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
            output.add(outContador, ContadorR);

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
        SistemaR model = new SistemaR();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new com.ms4systems.devs.core.simulation.impl.SimulationImpl("SistemaR Simulation",
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

    // Getter/setter for ContadorR
    public void setContadorR(Contador ContadorR) {
        propertyChangeSupport.firePropertyChange("ContadorR", this.ContadorR,
            this.ContadorR = ContadorR);
    }

    public Contador getContadorR() {
        return this.ContadorR;
    }

    // End getter/setter for ContadorR

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "ContadorR" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { ContadorR };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { Contador.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_CONTADORR:
                setContadorR((Contador) value);
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
            dirUri = SistemaR.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                SistemaR.class.getResource(".").toString());
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
        return new String[] { "Esperar", "Receber", "Enviar" };
    }
}
