package com.zzj.xiaomiwidgettest.view;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/18
 * @since 1.0.0
 */
public interface IRemoteView {
    /**
     * 添加视图到窗口管理器
     * @return true
     */
    boolean add();

    /**
     * 添加视图到串口管理器
     * @return true
     */
    boolean remove();

    /**
     * 是否已将视图添加到窗口管理器
     * @return true
     */
    boolean isAdd();
}
