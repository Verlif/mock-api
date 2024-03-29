package idea.verlif.test.global.domain;

import java.io.Serializable;

/**
 * @author Verlif
 */
public class User implements Serializable {

    private Integer userId;

    private String nickname;

    private Role.RoleKey roleKey;

    private Favorite favorite;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Role.RoleKey getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = Role.RoleKey.valueOf(roleKey);
    }

    public void setRoleKey(Role.RoleKey roleKey) {
        this.roleKey = roleKey;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    public User() {
    }
}
