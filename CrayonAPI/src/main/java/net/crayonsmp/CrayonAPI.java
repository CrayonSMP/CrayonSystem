package net.crayonsmp;

import net.crayonsmp.modules.CrayonModule;

import java.util.List;
import java.util.logging.Logger;

public interface CrayonAPI {

    Logger getLogger();

    List<CrayonModule> loadedModules();

    Boolean isDebugMode();

    String getPrefix();
}

