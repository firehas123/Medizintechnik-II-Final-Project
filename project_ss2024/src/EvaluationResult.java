public class EvaluationResult {
    private Double specificity;
    private Double sensitivity;

    // Constructor
    public EvaluationResult (Double specificity, Double sensitivity) {
        this.specificity = specificity;
        this.sensitivity = sensitivity;
    }

    // Getters
    public Double getSpecificity() {
        return specificity;
    }
    public Double getSensitivity() {
        return sensitivity;
    }
    //setters as the above values are private
    public void setSpecificity(Double specificity) {
        this.specificity = specificity;
    }
    public void setSensitivity(Double sensitivity) {
        this.sensitivity = sensitivity;
    }
}
