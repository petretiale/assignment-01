package pcd.shared.controller;

import pcd.shared.commands.Cmd;
import pcd.shared.view.View;

public interface ActiveController {
    void setView(View view);

    void notifyNewCmd(Cmd cmd);
}
