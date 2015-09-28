import core.*

def cli = new CliBuilder(
  usage:'java -jar ucdsl.jar [-f <file.dsl>] [-a] [-h] [-l] [-s] [-t]',
  header:'Default output: tgf files without node\'s label. \nOptions',
  stopAtNonOption:false
)

cli.with {
  f longOpt: 'file', args:1, argName:'file.dsl', 'DSL file, REQUIRED for tgf or asciidoc generation'
  h longOpt: 'help', 'Show usage information'
  t longOpt: 'tgf', "Export TGF file with node's label"
  l longOpt: 'label', "Export TGF file without node's label"
  a longOpt: 'asciidoc', 'Export formatted asciidoc documentation file'
  s longOpt: 'skeleton', 'Print a template use case in terminal'
}

def options = cli.parse(args)

if (!options) {
  return
}

if (options.h) {
  cli.usage()
  return
}

if (options.s) {
  println '''
Printing template use case:

system "Application Name", {
  usecase "Use Case Name", {
   version "1.0", type:"Creation", user:"Some User", date:"01/01/2014"

   actor user_alias, "Name or description of actor"

   preCondition "Pre-condition one", "Pre-condition two"

   basicFlow {
     step 1, user_alias, "do something", af:[1]
     step 2, system, "respond the action"
     step 3, user_alias, "do other thing", ef:[1]
     step 4, system, "respond the action"
   }

   alternative 1, "Alternative Flow Name", {
     step 1, user_alias, "do an alternative action"
     step 2, system, "respond the action", bfs:1
   }

   exception 1, "Exception Flow Name", {
     step 1, user_alias, "do a wrong action"
     step 2, system, "respond the wrong action", bfs:3
   }

   postCondition "Post-condition one", "Post-condition two"
 }
}

Done! Copy and paste in your editor.
'''
  return
}

if (!options.file) {
  cli.usage()
  return
}
else {
  if (!(options.a || options.l || options.t)) {
    println "Nothing to do with dsl file. Please inform desired action."
    println ''
    cli.usage()
    return
  }
}

def dsl = new File(options.file).text // getText('ISO-8859-1') //.text

def script = """

//******************************************************************************
//  Other Classes and functions
//******************************************************************************
def toSingleLinePattern = ~/\\s{2,}/
String.metaClass.toSingleLine = {
  (delegate =~ toSingleLinePattern).replaceAll(' ')
}

def propertyMissing(String name) {
  name
}

def bldr = new core.UseCaseBuilder()
def sistema = bldr.$dsl

if ($options.a) {
  println 'Creating asciidoc file for use cases:'
  println ''
  def m = new core.AsciidocBuilder()
  m.print(sistema)
  println ''
  println sistema.usecases.size + ' files created.'
}

if ($options.t || $options.l) {
  println 'Creating TGF for use cases:'
  println ''
  sistema.usecases.each {
    it.tgf.export(it, $options.l)
  }
  println ''
  println sistema.usecases.size + ' files created.'
}

println ''
"""

new GroovyShell().evaluate(script)
