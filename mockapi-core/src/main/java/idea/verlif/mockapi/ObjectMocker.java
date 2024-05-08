package idea.verlif.mockapi;

import idea.verlif.mockapi.config.PathRecorder;

public interface ObjectMocker {

    ObjectMocker DEFAULT = new DefaultObjectMocker();

    /**
     * 重置构建参数，例如path
     *
     * @param path 原始参数
     */
    default void resetPath(PathRecorder.Path path) {
        String oldPath = path.getPath();
        if (oldPath.isEmpty() || oldPath.charAt(0) == '/') {
            path.setPath("/mock" + oldPath);
        } else {
            path.setPath("/mock/" + oldPath);
        }
    }

    /**
     * 进行参数数据构造
     *
     * @param item mock 信息
     * @param pack 请求参数
     */
    Object mock(MockItem item, RequestPack pack);

    class DefaultObjectMocker implements ObjectMocker {
        @Override
        public Object mock(MockItem item, RequestPack pack) {
            return pack.getOldMethod().getName();
        }
    }

}
