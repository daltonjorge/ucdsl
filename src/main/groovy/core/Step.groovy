package core

class Step implements Vertex {
  int id
  Object actor
  String actorAlias
  String action
  def ef = []
  def af = []
  int bfs
  def parent
  int index

  String toString() {
    "${parent.alias()}#${String.format('%02d',id)}"
  }

  String description() {
    String flowName = (!this.parent.class.name.equals('core.BasicFlow') && this.id == 1) ? "(${this.parent.name})" : ''

    def actionType = this.actorAlias.equals('Sistema') ? '[e]' : '[s]'
    "${actionType} ${flowName} ${actorAlias} ${action.toSingleLine()}"
  }

  String label() {
    this.toString().replace('#', '.')
  }

  String transitionTo(node) {
    "${this.index} ${node} ${this.description()}"
  }

  def transitionToBfs() {
    def result = []
    if(this.bfs) {
      def stepFound = this.parent.parent.basicFlow.steps.find {it.id == this.bfs}
      if(!stepFound) {
        notFoundAndExit('FB#' + String.format('%02d', this.bfs))
      }
      result = this.transitionTo(stepFound.index)
    }
    result
  }

  def transitionsToFlow(flows, prefix) {
    def result = []
    def nos = this.parent.parent.tgf.nodes
    flows.each { flow ->
      def no = nos.find {
        it.toString() == "${prefix}${String.format('%02d', flow)}#01"
      }
      if(!no) {
        notFoundAndExit(prefix + String.format('%02d', flow))
      }
      result <<
        "${this.index - 1} ${no.index} ${nos.get(this.index - 2).description()}"
    }
    result
  }

  void notFoundAndExit(String state) {
    println "Error on '${this.parent.name}': ${state} not found!"
    println "Use Case: ${this.parent.parent.name}"
    println "Operation aborted!"
    System.exit(0)
  }
}
