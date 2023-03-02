package idea.verlif.mockapi.global.domain.req;

/**
 * @author Verlif
 */
public class UpdatePassword {

    /**
     * 原密码
     */
    private String old;

    /**
     * 新密码
     */
    private String now;

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }
}
