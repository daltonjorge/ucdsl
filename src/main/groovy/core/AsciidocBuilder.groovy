package core

class AsciidocBuilder {
  def lines = []

  void print(application) {
    new File("asciidoc").mkdir()
    application.usecases.each { uc ->
      lines.clear()
      def file = new File("asciidoc/${uc.name.tokenize().join('_')}.adoc").newWriter("UTF-8", true)
      lines << "= Especificação de Casos de Uso do $application.name"
      lines << "SPLab/Ingenico"
      lines << ":revnumber: v${uc.versions?.last().number}"
      lines << ":revdate: {docdatetime}"
      lines << ":pagenums:"
      lines << ":toc:"
      lines << ":imagesdir: assets/images"
      lines << ":homepage: http://asciidoctor.org"
      lines << ":numbered:"
      lines << ":toc-title: SUMÁRIO"
      lines << ":toclevels: 3"
      lines << ":imagesdir: ./myimages"
      lines << ""
      lines << "== Informações sobre o Documento"
      lines << "\'\'\'"
      lines << "=== Histórico"
      lines << "[cols=\"<10,<40,^25,^14\"]"
      lines << "|==="
      lines << "|Versão |Tipo de Modificação |Autor | Data"
      lines << ""
      uc.versions?.each {
        lines << "|${it.number}"
        lines << "|${it.type}"
        lines << "|${it.user}"
        lines << "|${it.date}"
      }
      lines << "|==="
      lines << "=== Objetivo"
      lines << "Este documento descreve o caso de uso \"${uc.name}\", pertencente ao sistema \"${application.name}\"."
      lines << ""
// TODO: Incluir opcao de referencia de documentos na dsl
//      lines << "=== Referência"
//      lines << "Para melhor compreensão deste documento é aconselhável a leitura dos seguintes documentos:"
//      lines << ""
//      lines << "image::logo.png[]"
//      lines << ""
      lines << "<<<"
      lines << "== Caso de Uso: ${uc}"
      lines << "\'\'\'"
      lines << "=== Finalidade"
      lines << "=== Usuário/Ator"
      lines << "[cols=\"^29,60\"]"
      lines << "|==="
      lines << "|Usuário/Ator |Descrição"
      lines << ""
      uc.actors.each {
        lines << "|${it.name}"
        lines << "|${it.name}"
      }
      lines << "|==="
      lines << ""
      doConditions(uc.preConditions, 'Pré-condições')
      lines << "=== Fluxos"
      doBasicFlow(uc.basicFlow)
      lines << ""
      doFlows(uc.alternatives, 'Fluxos Alternativos')
      doFlows(uc.exceptions, 'Fluxos de Exceção')
      doConditions(uc.postConditions, 'Pós-condições')
      file.write(lines.join('\n'))
      file.close()
      println "> '${uc.name}' done!"
    }
  }

  private def doConditions(conditions, title) {
    if (conditions) {
      lines << "=== ${title}"
      lines << "[cols=\"^2,7\"]"
      lines << "|==="
      lines << "|ID |${title}"
      lines << ""
      conditions?.description?.eachWithIndex { it, i ->
        lines << "|${conditions.alias}${String.format('%02d',++i)}"
        lines << "|$it"
      }
      lines << "|==="
    }
  }

  private def doBasicFlow(basicFlow) {
    if(basicFlow) {
      lines << "==== Fluxo Básico"
      basicFlow.steps?.each { st ->
        lines << "${st.id}. ${st.actorAlias.capitalize()} ${st.action} " +
          // "${st.action.replaceAll( /( )\1+/, '$1' )}. " +
          st.af.collect { "[FA${String.format('%02d',it)}]" }.join() +
          st.ef.collect { "[FE${String.format('%02d',it)}]" }.join()
      }
    }
  }

  private def doFlows(flows, title) {
    if(flows) {
      lines << "==== $title"
      flows?.each{ f ->
        lines << "===== ${f.alias()}: ${f}"
        f.steps?.each{ st ->
          lines << "${st.id}. ${st.actorAlias.capitalize()} ${st.action} " +
          st.ef.collect { "[FE${String.format('%02d',it)}]" }.join() +
          "${st.bfs ? '[FB#'+String.format('%02d',st.bfs)+']' : ''}"
          lines << ''
        }
      }
    }
  }
}
