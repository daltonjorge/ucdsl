package core

//******************************************************************************
//  Builder Class
//******************************************************************************
class UseCaseBuilder extends FactoryBuilderSupport {
  {
    registerFactory("system", new ApplicationFactory())
    registerFactory("usecase", new UseCaseFactory())
    registerFactory("version", new VersionFactory())
    registerFactory("preCondition", new PreConditionFactory())
    registerFactory("postCondition", new PostConditionFactory())
    registerFactory("basicFlow", new BasicFlowFactory())
    registerFactory("alternative", new AlternativeFlowFactory())
    registerFactory("exception", new ExceptionFlowFactory())
    registerFactory("step", new StepFactory())
    registerFactory("actor", new ActorFactory())
  }
}

//******************************************************************************
//  Factories Classes
//******************************************************************************
class ApplicationFactory extends AbstractFactory {
  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new Application(name: value)
  }

  void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
    parent.usecases << child
  }
}

class UseCaseFactory extends AbstractFactory {
  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new UseCase(name: value)
  }
}

class PreConditionFactory extends AbstractFactory {
  boolean isLeaf() { true }

  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    def conditions = value.class == String ? [value] : value
    new PreCondition(alias:'PC', description: conditions)
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    parent.tgf << child
    parent.preConditions = child
  }
}

class PostConditionFactory extends AbstractFactory {
  boolean isLeaf() { true }

  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    def conditions = value.class == String ? [value] : value
    new PostCondition(alias:'PO', description:conditions)
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    parent.tgf << child
    parent.postConditions = child
  }
}

class BasicFlowFactory extends AbstractFactory {
  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new BasicFlow(name:"Fluxo Básico")
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    child.parent = parent
    parent.basicFlow = child
  }
}

class AlternativeFlowFactory extends AbstractFactory {
  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new AlternativeFlow(id:value[0], name:value[1])
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    child.parent = parent
    parent.alternatives << child
  }
}

class ExceptionFlowFactory extends AbstractFactory {
  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new ExceptionFlow(id:value[0], name:value[1])
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    child.parent = parent
    parent.exceptions << child
  }
}

class StepFactory extends AbstractFactory {
  boolean isLeaf() { true }

  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new Step(id:value[0], actorAlias:value[1], action: value[2])
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    if(child.actorAlias == 'system') {
      child.actorAlias = 'Sistema'
      child.actor = parent.parent
    } else {
      def actor = parent.parent.actors.find { it.alias == child.actorAlias }
      if(!actor) {
        println "Actor '${child.actorAlias}' not found!"
        println "Parsing aborted."
        System.exit(0)
      }
      else {
        child.actor = actor
        child.actorAlias = actor.name
      }
    }
    parent.parent.tgf << child
    child.parent = parent
    parent.steps << child
  }
}

class VersionFactory extends AbstractFactory {
  boolean isLeaf() { true }

  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new Version(number:value)
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    parent.versions << child
  }
}

class ActorFactory extends AbstractFactory {
  boolean isLeaf() { true }

  def newInstance(FactoryBuilderSupport builder, name, value, Map attributes ) {
    new Actor(alias:value[0], name:value[1])
  }

  void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    parent.actors << child
  }
}
