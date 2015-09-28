package core

abstract class Condition implements Vertex {
  String alias
  def description = []
  int index

  String toString() { alias }

  String description() {
    "[c] ${this.description?.collect{ it[it.size() - 1] != '.' ? it + '.' : it }.join(' ')}"
  }

  String label() {
    this.toString().replace('#', '.')
  }
}
