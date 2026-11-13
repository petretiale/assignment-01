package pcd.common.controller;

import pcd.common.commands.Cmd;
import pcd.common.view.View;

public interface ActiveController {
    void setView(View view);

    void notifyNewCmd(Cmd cmd);
}
