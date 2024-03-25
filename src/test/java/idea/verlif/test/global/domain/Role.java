package idea.verlif.test.global.domain;

import java.io.Serializable;

/**
 * @author Verlif
 */
public class Role implements Serializable {

    private Integer roleId;

    private String roleName;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 角色Key
     */
    public enum RoleKey {
        /**
         * 访客
         */
        VISITOR,
        /**
         * 注册用户
         */
        USER,
        /**
         * 管理员
         */
        ADMIN
    }
}
