/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/ProjetoIC/src/Models/dnl/SistemaR.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class Contador implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:Contador:0
    Integer value;

    //ENDIF
    public Contador() {
    }

    public Contador(Integer value) {
        this.value = value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public String toString() {
        String str = "Contador";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}
