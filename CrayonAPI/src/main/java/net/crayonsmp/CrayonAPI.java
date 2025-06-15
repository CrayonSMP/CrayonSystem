package net.crayonsmp;

import net.crayonsmp.interfaces.CrayonGoalService;
import net.crayonsmp.interfaces.CrayonModule;
import net.crayonsmp.utils.config.SConfig;

import java.util.List;
import java.util.logging.Logger;

public interface CrayonAPI {

    Logger getLogger();

    List<CrayonModule> loadedModules();

    Boolean isDebugMode();

    String getPrefix();

    SConfig getConfig();

    CrayonGoalService CrayonGoalService();
}

