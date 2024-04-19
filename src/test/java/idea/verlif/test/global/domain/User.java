package idea.verlif.test.global.domain;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户拥有的角色Key列表
     */
    private List<Role.RoleKey> roleKeys;

    /**
     * 用户喜好列表
     */
    private List<Favorite> favorites;

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

    public List<Role.RoleKey> getRoleKeys() {
        return roleKeys;
    }

    public void setRoleKeys(List<Role.RoleKey> roleKeys) {
        this.roleKeys = roleKeys;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }

    public User() {
    }
}
