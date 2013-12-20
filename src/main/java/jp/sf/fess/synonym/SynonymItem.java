package jp.sf.fess.synonym;

import java.util.Arrays;

public class SynonymItem {
    private final String[] inputs;

    private final String[] outputs;

    private String[] newInputs;

    private String[] newOutputs;

    private final int id;

    public SynonymItem(final int id, final String[] inputs,
            final String[] outputs) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        Arrays.sort(inputs);
        if (inputs != outputs) {
            Arrays.sort(outputs);
        }
    }

    public int getId() {
        return id;
    }

    public String[] getNewInputs() {
        return newInputs;
    }

    public void setNewInputs(final String[] newInputs) {
        this.newInputs = newInputs;
    }

    public String[] getNewOutputs() {
        return newOutputs;
    }

    public void setNewOutputs(final String[] newOutputs) {
        this.newOutputs = newOutputs;
    }

    public String[] getInputs() {
        return inputs;
    }

    public String[] getOutputs() {
        return outputs;
    }

    public boolean isUpdated() {
        return newInputs != null && newOutputs != null;
    }

    public void sort() {
        if (inputs != null) {
            Arrays.sort(inputs);
        }
        if (outputs != null) {
            Arrays.sort(outputs);
        }
        if (newInputs != null) {
            Arrays.sort(newInputs);
        }
        if (newOutputs != null) {
            Arrays.sort(newOutputs);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(inputs);
        result = prime * result + Arrays.hashCode(outputs);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SynonymItem other = (SynonymItem) obj;
        sort();
        other.sort();
        if (!Arrays.equals(inputs, other.inputs)) {
            return false;
        }
        if (!Arrays.equals(outputs, other.outputs)) {
            return false;
        }
        return true;
    }

}
