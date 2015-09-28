package core

class UseCase {
  String name
  def actors = []
  Flow basicFlow
  def exceptions = []
  def alternatives = []
  def versions = []
  def preConditions
  def postConditions
  def skips = []
  TGFBuilder tgf = new TGFBuilder()
  String toString() { name }
}
