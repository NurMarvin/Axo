package de.nurmarvin.axo.level;

public class LevelInfo {
    private long level;
    private long xp;
    private long totalXp;

    public LevelInfo() {
        this.level = 1;
        this.xp = 0;
        this.totalXp = 0;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public boolean addXp(long xp) {
        this.xp += xp;
        this.totalXp += xp;
        long reach = this.xpRequiredForNextLevel();
        if (this.xp >= reach && reach != 0) {
            this.xp = 0;
            this.level++;
            return true;
        }
        return false;
    }

    private long xpRequiredForNextLevel() {
        return (long) Math.floor(5.5 * (Math.pow(this.level, 2)) + 75 * this.level + 100);
    }

    public static void main(String[] args) {
        StringBuilder labels = new StringBuilder("labels: [");
        StringBuilder data = new StringBuilder("data: [");
        for(int i = 1; i <= 100; i++) {
            LevelInfo levelInfo = new LevelInfo();
            levelInfo.setLevel(i);
            if(i != 1) {
                labels.append(", ");
                data.append(", ");
            }
            labels.append(String.format("'Level %s'", levelInfo.level));
            data.append(levelInfo.xpRequiredForNextLevel());
        }
        System.out.println(labels + "],");
        System.out.println(data + "],");
    }
}
