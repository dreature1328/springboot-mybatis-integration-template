package dreature.smit.entity;

public class Data {
    // 属性
    private String id;
    private String attr1;
    private String attr2;

    // 无参构造函数
    public Data() {
    }

    // 成员列表构造函数
    public Data(
            String id,
            String attr1,
            String attr2
    ) {
        this.id = id;
        this.attr1 = attr1;
        this.attr2 = attr2;
    }

    // 复制构造函数
    public Data(Data data) {
        this.id = data.getId();
        this.attr1 = data.getAttr1();
        this.attr2 = data.getAttr2();
    }

    // Getter 方法
    public String getId() {
        return id;
    }
    public String getAttr1() {
        return attr1;
    }
    public String getAttr2() {
        return attr2;
    }

    // Setter 方法
    public void setId(String id) {
        this.id = id;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }


    // 重写 toString 方法
    @Override
    public String toString() {
        return
            "Data["
            + "id=" + id + ", "
            + "attr1=" + attr1 + ", "
            + "attr2=" + attr2 + ", "
            + "]";
    }
}
