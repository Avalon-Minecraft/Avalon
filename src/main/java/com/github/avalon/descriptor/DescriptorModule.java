package com.github.avalon.descriptor;

import com.github.avalon.annotation.annotation.Module;
import com.github.avalon.block.block.Block;
import com.github.avalon.console.logging.DefaultLogger;
import com.github.avalon.fluid.Fluid;
import com.github.avalon.item.Item;
import com.github.avalon.module.ServerModule;
import com.github.avalon.server.IServer;

@Module(name = "Descriptor Module", asynchronous = true)
public class DescriptorModule extends ServerModule {

  public static final DefaultLogger LOGGER = new DefaultLogger(DescriptorModule.class);

  private final DescriptorContainer registry;

  public DescriptorModule(IServer host) {
    super(host);

    registry = new DescriptorContainer();
  }

  @Override
  public void enable() {
    super.enable();
    registerDescriptors();
  }

  public void registerDescriptors() {
    registerTagDescriptor(Block.class, "blocks");
    registerTagDescriptor(Item.class, "items");
    registerTagDescriptor(Fluid.class, "fluids");
    registerTagDescriptor(Character.class, "characters");
  }

  public <T> void registerTagDescriptor(Class<T> clazz, String name) {
    runTaskAsynchronously(
        () -> {
          ClassDescriptor<T> descriptor = new ClassDescriptor<>(name);
          descriptor.scanForDescriptors(clazz);

          assert !descriptor.getClasses().isEmpty()
              : name + " is empty. Descriptors of this type should not be empty!";

          registry.createDescriptor(name, descriptor);

          LOGGER.info(
              "Tag descriptor for %s has been created. Found %s tags.",
              name, descriptor.getClasses().size());
        });
  }

  public DescriptorContainer getRegistry() {
    return registry;
  }
}
