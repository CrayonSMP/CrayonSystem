package net.crayonsmp.crayonCore;

import de.bluecolored.bluemap.api.BlueMapAPI;
import net.crayonsmp.CrayonAPI;
import net.crayonsmp.CrayonModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;


public class CrayonCore extends JavaPlugin implements CrayonAPI {

    private final List<CrayonModule> loadedModules = new ArrayList<>();
    private void onBlueMapApiLoaded(BlueMapAPI api){
        for (CrayonModule module : loadedModules){
            module.OnBlueMapEnabled(api);
        }
    }
    @Override
    public void onLoad() {
        getLogger().info("Loading CrayonCore...");
        getLogger().info("Scanning for modules...");


        String modulePackage = "net.crayonsmp"; // <--- HIER ANPASSEN!

        Reflections reflections;
        try {
            reflections = new Reflections(modulePackage);
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "Failed to initialize Reflections for package: " + modulePackage, t);
            return; // Ohne Reflections können keine Module geladen werden
        }

        Set<Class<? extends CrayonModule>> moduleClasses = reflections.getSubTypesOf(CrayonModule.class);
        getLogger().info("Found " + moduleClasses.size() + " potential module classes in package: " + modulePackage);

        for (Class<? extends CrayonModule> moduleClass : moduleClasses) {
            String moduleClassName = moduleClass.getName();
            getLogger().info("Attempting to load module class: " + moduleClassName);
            try {
                // 1. Instanziieren (stellt sicher, dass es einen öffentlichen Standardkonstruktor gibt)
                getLogger().info("  Instantiating " + moduleClassName + "...");
                CrayonModule module = moduleClass.getDeclaredConstructor().newInstance();
                getLogger().info("  Successfully instantiated " + moduleClassName);

                // 2. Modul onLoad aufrufen
                getLogger().info("  Calling onLoad() for " + module.getName() + " (" + moduleClassName + ")...");
                module.onLoad(this); // Ruft die onLoad in der Modulklasse (z.B. Main.java) auf
                getLogger().info("  Finished onLoad() for " + module.getName());

                // 3. Zur Liste hinzufügen
                getLogger().info("  Adding " + module.getName() + " to loaded modules list...");
                loadedModules.add(module);
                getLogger().info("  Successfully loaded and processed module: " + module.getName());

            } catch (Throwable e) { // Fange Throwable, um alle Fehler (auch Errors) zu erwischen
                // Logge den Fehler ausführlich
                getLogger().log(Level.SEVERE, "!!! CRITICAL FAILURE loading module: " + moduleClassName + " !!!", e);
                // Fahre mit dem nächsten Modul fort, anstatt komplett abzubrechen
            }
        }
        BlueMapAPI.onEnable(this::onBlueMapApiLoaded);
        getLogger().info("Finished module loading sequence. Total modules loaded: " + loadedModules.size());
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabling CrayonCore...");
        getLogger().info("Number of loaded modules to enable: " + loadedModules.size());

        if (loadedModules.isEmpty()) {
            getLogger().warning("No modules were loaded successfully. Check previous logs for errors during onLoad.");
        }

        for (CrayonModule module : loadedModules) {
            String moduleName = module.getName();
            getLogger().info("Attempting to enable module: " + moduleName + " (" + module.getClass().getName() + ")");
            try {
                // Modul onEnable aufrufen (hier wird der Command registriert)
                module.onEnable(this); // Übergibt das CrayonCore Plugin-Objekt
                getLogger().info("Successfully enabled module: " + moduleName);

            } catch (Throwable e) { // Auch hier Throwable fangen
                getLogger().log(Level.SEVERE, "!!! CRITICAL FAILURE enabling module: " + moduleName + " !!!", e);
                // Optional: Modul aus der Liste entfernen oder als 'deaktiviert' markieren
            }
        }
        getLogger().info("Finished enabling modules.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling CrayonCore...");
        getLogger().info("Number of loaded modules to disable: " + loadedModules.size());
        for (CrayonModule module : loadedModules) {
            try {
                getLogger().info("Disabling module: " + module.getName());
                module.onDisable();
                getLogger().info("Successfully disabled module: " + module.getName());
            } catch (Throwable e) {
                getLogger().log(Level.SEVERE,"Error disabling module " + module.getName(), e);
            }
        }
        loadedModules.clear(); // Liste leeren
        getLogger().info("Finished disabling modules.");
    }

    @Override
    public List<CrayonModule> loadedModules() {
        return loadedModules;
    }
}