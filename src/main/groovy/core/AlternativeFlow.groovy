package core

class AlternativeFlow extends Flow {
  String alias() { "FA${String.format('%02d',id)}" }
}
