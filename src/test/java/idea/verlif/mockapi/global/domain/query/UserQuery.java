package idea.verlif.mockapi.global.domain.query;

/**
 * @author Verlif
 */
public class UserQuery {

    private String nickname;

    public void setNickname(String nickname) {
        if (!"".equals(nickname)) {
            this.nickname = nickname;
        }
    }

    public String getNickname() {
        return nickname;
    }
}
