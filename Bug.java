public enum Bug {
    QUEEN ('Q', "bee", "queen.png", 0),
    BEETLE ('B', "beetle", "beetle.png", 4),
    GRASSHOPPER ('H', "grasshopper", "grasshopper.png", 3),
    SPIDER ('S', "spider", "spider.png", 2),
    ANT ('A', "ant", "ant.png", 1);
    
    private final char abbrev;
    private final String name;
    private final String imgFile;
    private final int index;
    
    public static final Bug[] BUGS = new Bug[]{
            QUEEN, ANT, SPIDER, GRASSHOPPER, BEETLE 
    };
    
    Bug(char abbrev, String name, String file, int i) {
        this.abbrev = abbrev;
        this.name = name;
        this.imgFile = file;
        this.index = i;
    }
    
    public static Bug fromAbbrev(char abbrev) {
        switch (abbrev) {
            case 'Q':
                return Bug.QUEEN;
            case 'B':
                return Bug.BEETLE;
            case 'H':
                return Bug.GRASSHOPPER;
            case 'S':
                return Bug.SPIDER;
            case 'A':
                return Bug.ANT;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public static Bug fromAbbrev(String abbrev) {
        if (abbrev.length() == 1) {
            return fromAbbrev(abbrev.charAt(0));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public char getAbbrev() {
        return abbrev;
    }
    
    public String getName() {
        return name;
    }
    
    public String getIMGFile() {
        return imgFile;
    }
    
    public int getIndex() {
        return index;
    }
}
