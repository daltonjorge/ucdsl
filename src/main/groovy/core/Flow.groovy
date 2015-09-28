package core

abstract class Flow {
  int id
  def steps = []
  def parent
  String name
  String toString() { name }
  abstract String alias()
}
