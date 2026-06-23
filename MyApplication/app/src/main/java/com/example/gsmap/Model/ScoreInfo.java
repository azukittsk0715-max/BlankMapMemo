public class ScoreInfo {
    private String walker_id;
    private Integer score;

    public ScoreInfo(String walker_id, Integer score) {
        this.walker_id = walker_id;
        this.score = score;
    }

    public String getWalkerId() {
        return walker_id;
    }

    public Integer getScore() {
        return score;
    }

    public void setWalkerId(String walker_id) {
        this.walker_id = walker_id;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}