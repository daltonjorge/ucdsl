package core

class TGFBuilder {
  int count = 0
  int nodes_index = 0
  def nodes = []
  def states = []
  def transitions = []
  private def usecase
  private def printLabel

  def leftShift(vertex) {
    vertex.index = ++count
    nodes += vertex
  }

  def visit(PreCondition c) {
    transitions << "1 2 ${c.description()}"
  }

  def visit(PostCondition c) {
    nodes_index += 1
    states << "${nodes_index} ${printLabel ? 'FIM_' + c.label() : nodes_index}"
    transitions << "${c.index} ${nodes_index} ${c.description()}"
  }

  def visit(Step s) {
    if(s != s.parent.steps.last()) {
      transitions << s.transitionTo(s.index + 1)
    }
    else if (s.parent.class == core.BasicFlow) {
      int poIndex = nodes.find { it.toString() == 'PO' }.index
      transitions << s.transitionTo(poIndex)
    }
    else if (s.parent.class in [core.ExceptionFlow, core.AlternativeFlow] && !s.bfs) {
      nodes_index += 1
      states << "${nodes_index} ${printLabel ? 'FIM_' + s.label() : nodes_index}"
      transitions << s.transitionTo(nodes_index)
    }
    transitions += s.transitionsToFlow(s.af, 'FA')
    transitions += s.transitionsToFlow(s.ef, 'FE')
    transitions += s.transitionToBfs()
  }

  def export(usecase, printLabel) {
    this.usecase = usecase
    this.printLabel = printLabel
    new File("tgf").mkdir()
    def file = new File("tgf/${usecase.name.tokenize().join('_')}.tgf")
    states = nodes.collect {"${it.index} ${printLabel ? it.label() : it.index}"}
    nodes_index = nodes[nodes.size - 1].index
    nodes.each {
      visit(it)
    }
    states << '#'
    file.write((states + transitions).join('\n'))
    println "> '${usecase.name}' done!"
  }
}
