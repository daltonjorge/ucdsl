package core

class MarkdownBuilder {
  def lines = []

  void print(application) {
    new File("markdown").mkdir()
    lines << "Sistema: $application.name"
    application.usecases.each { uc ->
      def file = new File("markdown/${uc.name.tokenize().join('_')}.md")
      lines << "  Caso de Uso: ${uc}"
      if(uc.actors.size == 1) {
        lines << "    Ator: ${uc.actors[0]}"
      } else if(uc.actors.size > 1) {
        lines << "    Atores: ${uc.actors.join(', ')}"
      }
      if (uc.preConditions) {
        lines << ''
        lines << '    Pré Condições'
      }
      uc.preConditions?.description?.eachWithIndex { it, i ->
        lines << "      [PC${String.format('%02d',i)}] $it"
      }
      if(uc.basicFlow) {
        lines << ''
        lines << "    ${uc.basicFlow}"
        uc.basicFlow.steps?.each{ st ->
          lines <<  "      ${st.id}. " +
            "${uc.actors.find{ it.alias.equals(st.actorAlias)}} " +
            "${st.action.replaceAll( /( )\1+/, '$1' )}. " +
            "${st.af && uc.alternatives?.find{ it.id == st.af } ? '[FA'+String.format('%02d',st.af)+']'  : ''}" +
            "${st.ef && uc.exceptions?.find{ it.id == st.ef } ? '[FE'+String.format('%02d',st.ef)+']'  : ''}"
        }
      }
      uc.alternatives?.each{ af ->
        lines << ''
        lines << "    Fluxo Alternativo [FA${String.format('%02d',af.id)}]: $af"
        af.steps?.each{ st ->
          lines << "      ${st.id}. ${uc.actors.find{ it.alias == st.actorAlias}} ${st.action.toSingleLine()}" +
            "${st.ef && uc.exceptions?.find{ it.id == st.ef } ? '[FE'+String.format('%02d',st.ef)+']'  : ''}" +
            "${st.bfs ? '. [FB#'+String.format('%02d',st.bfs)+']'  : ''}"
          lines << ''
        }
      }
      uc.exceptions?.each{ ef ->
        lines << ''
        lines << "    Fluxo de Exceção [FE${String.format('%02d',ef.id)}]: $ef"
        ef.steps?.each{ st ->
          lines << "      ${st.id}. ${uc.actors.find{ it.alias == st.actorAlias}} ${st.action}" +
            "${st.bfs ? '. [FB#'+String.format('%02d',st.bfs)+']'  : ''}"
          lines << ''
        }
      }
      if (uc.postConditions) {
        lines << ''
        lines << '    Pós Condições'
      }
      uc.postConditions?.description?.eachWithIndex { it, i ->
        lines << "      [PO${String.format('%02d',i)}] $it"
      }
      file.write(lines.join('\n'))
      println "> '${uc.name}' done!"
    }
  }

}