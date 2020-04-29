import re
import sys
from sly import Lexer
from sly import Parser
import warnings

class SppLexer(Lexer):
    # Set of token names.   This is always required
    tokens = { TYPE, ID, NUMBER, REALNUMBER, WORD, LETTER, ASSIGN,
               IF, ELSE, WHILE, DO, DONE, FOR, RETURN, STATIC,
               EQ, NE, LE, ME, OR, AND}
    ignore = '\t '
    literals = { '+', '-', '/', '*', '(', ')', '{', '}', ',', ';', '>', '<', '!', '.'}

    REALNUMBER = r'[+-]?[0-9]+\.[0-9]+'
    TYPE = r'(number)|(word)|(letter)|(real)'
    STATIC = r'static'
    IF = r'if'
    ELSE = r'else'
    WHILE = r'while'
    DONE = r'done'
    DO = r'do'
    FOR = r'for'
    RETURN = r'return'

    ID      = r'[a-zA-Z_][a-zA-Z0-9_]*'
    NUMBER  = r'\d+'
    EQ = r'=='
    ASSIGN  = r'='
    NE = r'!='
    LE = r'<='
    ME = r'>='
    OR = r'\|\|'
    AND = r'&&'


    @_(r'\".*?\"')
    def WORD(self, t):
        t.value = t.value.replace('"', '')
        return t

    @_(r'\'.*?\'')
    def LETTER(self, t):
        if len(t.value) < 4:
            t.value = '{}'.format(t.value[1])
            return  t
        else:
            print('Line %d: Bad input %r' % (self.lineno, t.value))
            return None

    @_(r'\n+')
    def ignore_newline(self, t):
        self.lineno += t.value.count('\n')

    @_(r'\d+')
    def NUMBER(self, t):
        t.value = int(t.value)   # Convert token to numeric
        return t

    @_(r'#.*#')
    def COMMENT(self, t):
        pass

    @_(r'//.*')
    def COMMENT2(self, t):
        pass

    def error(self, t):
        print('Line %d: Bad character %r' % (self.lineno, t.value[0]))
        self.index += 1



class SppParser(Parser):

    tokens = SppLexer.tokens

    precedence = (
        ('left', '>', '<', LE, ME, EQ, NE, OR, AND),
        ('left', '!'),
        ('left', '+', '-'),
        ('left', '*', '/'),
        ('right', 'UMINUS'),
    )


    @_('statement statements')
    def statements(self, p):
        return ('program', p[0], p[1])

    @_('statement')
    def statements(self, p):
        return ('program', p[0], None)

    @_('statement RETURN statement')
    def statements(self, p):
        return ('program', p[0], ('program', p[2], None, p[1]))

    # statements

    # function definition

    @_('function_definition')
    def statement(self, p):
        return p.function_definition

    @_('ID "(" func_vars ")" bracket_statements')
    def function_definition(self, p):
        return ('func_def', None, p.ID, p.func_vars, p.bracket_statements)

    @_('TYPE ID "(" func_vars ")" return_bracket_statements')
    def function_definition(self, p):
        return ('func_def', p.TYPE, p.ID, p.func_vars, p.return_bracket_statements)

    @_('ID "(" ")" bracket_statements')
    def function_definition(self, p):
        return ('func_def', None, p.ID, None, p.bracket_statements)

    @_('TYPE ID "(" ")" return_bracket_statements')
    def function_definition(self, p):
        return ('func_def', p.TYPE, p.ID, None, p.return_bracket_statements)

    #function definition end

    # function define variables start

    @_('var_declare "," func_vars')
    def func_vars(self, p):
        return ("func_var", p.var_declare, p.func_vars);

    @_('var_declare')
    def func_vars(self, p):
        return ("func_var", p.var_declare, None);

    @_('expr "," func_call_vars')
    def func_call_vars(self, p):
        return ('func_call_var', p.expr, p.func_call_vars)

    @_('expr')
    def func_call_vars(self, p):
        return ('func_call_var', p.expr, None)

    # function define variables end

    # calling functions start

    @_('variable_function_call')
    def statement(self, p):
        return p.variable_function_call

    @_('ID "." expr')
    def variable_function_call(self, p):
        if p.expr[1] == "Previous":
            return ('func_call', p.expr[1], p.ID, p.lineno)
        else:
            return ('func_call', p.expr[1], ('func_call_var', ('var', p.ID, p.lineno), None), p.lineno)

    # calling functions end

    # loops start

    @_('FOR "(" var_assign ";" expr ";" var_assign ")" bracket_statements',
       'FOR "(" var_assign ";" expr ";" var_assign ")" statement')
    def statement(self, p):
        return ('for_loop', p.var_assign0, p.expr, p.var_assign1, p[8], p.lineno)

    @_('WHILE "(" expr ")" bracket_statements',
       'WHILE "(" expr ")" statement')
    def statement(self, p):
        return ('while_loop', p.expr, p[4], p.lineno)

    # loops end

    @_('if_statement')
    def statement(self, p):
        return p.if_statement

        # if and else statements start

    @_('IF "(" expr ")" bracket_statements',
       'IF "(" expr ")" statement')
    def if_statement(self, p):
        return ('if_stmt', p.expr, p[4], None, p.lineno)

    @_('IF "(" expr ")" bracket_statements else_statement',
       'IF "(" expr ")" statement else_statement')
    def if_statement(self, p):
        return ('if_stmt', p.expr, p[4], p.else_statement, p.lineno)

    @_('ELSE bracket_statements',
       'ELSE statement')
    def else_statement(self, p):
        return p[1]

    @_('DO statements DONE')
    def bracket_statements(self, p):
        return p.statements

    @_( 'DO statements DONE',
        'DO RETURN expr DONE',
        'DO RETURN variable_function_call DONE',)
    def return_bracket_statements(self, p): # reikia padaryti kad eitu declare darti is naujo kvieciant funkcija
        if(len(p) == 4):
            return ('program', p[2], None, p[1])
        else:
            return p[1]

    @_( 'var_assign',
        'var_declare',
        'expr')
    def statement(self, p):
        return p[0]

    @_('expr "+" expr',
       'expr "-" expr',
       'expr "*" expr',
       'expr "/" expr',
       'expr NE expr',
       'expr EQ expr',
       'expr ">" expr',
       'expr "<" expr',
       'expr LE expr',
       'expr ME expr',
       'expr OR expr',
       'expr AND expr')
    def expr(self, p):
        return ('expr', p[1], p.expr0, p.expr1, p.lineno)

    @_('"-" expr %prec UMINUS')
    def expr(self, p):
        return -p.expr

    @_('"!" expr')
    def expr(self, p):
        return ('not', p.expr, p.lineno)

    @_('"(" expr ")"')
    def expr(self, p):
        return p.expr

    @_('ID "(" ")" ')
    def expr(self, p):
        return ('func_call', p.ID, None, p.lineno)

    @_('ID "(" func_call_vars ")" ')
    def expr(self, p):
        return ('func_call', p.ID, p.func_call_vars, p.lineno)

    @_('NUMBER')
    def expr(self, p):
        return ("numberValue", int(p.NUMBER))

    @_('LETTER')
    def expr(self, p):
        return ("letterValue", p.LETTER)

    @_('WORD')
    def expr(sel, p):
        return ("wordValue", p.WORD)

    @_('REALNUMBER')
    def expr(sel, p):
        try:
            return ("realValue", float(p.REALNUMBER))
        except :
            pass

    @_('ID')
    def expr(self, p):
        return ('var', p.ID, p.lineno)

    @_( 'var_declare ASSIGN expr',
        'var_declare ASSIGN variable_function_call',
        'ID ASSIGN variable_function_call',
        'ID ASSIGN expr')
    def var_assign(self, p):
        return ('var_assign', p[0], p[2], p.lineno)

    @_('TYPE ID')
    def var_declare(self, p):
        return ('var_declare', p.TYPE, p.ID, False, p.lineno)

    @_('STATIC TYPE ID')
    def var_declare(self, p):
        return ('var_declare', p.TYPE, p.ID, True, p.lineno)

    def error(self, string):
        print("Error " + string.type + " at line: " + str(string.lineno) + " at index: " + str(string.index) )
        main()

class SppExecute:

    def __init__(self, tree, env, prevEnv):
        self.env = env
        self.prevEnv = prevEnv

        result = self.walkTree(tree)
        if result is not None and isinstance(result, int) or isinstance(result, bool):
            print(result)
        if isinstance(result, str) and result[0] == '"':
            print(result)

    def walkTree(self, node):
        if isinstance(node, int):
            return node
        if isinstance(node, str):
            return node
        if isinstance(node, bool):
            return node
        if node is None:
            return None

        if node[0] == 'program':
            if node[2] == None and len(node) == 4 and node[3] == 'return':
                return ('return', self.walkTree(node[1]))
            if node[2] == None:
                return self.walkTree(node[1])
            else:
                self.walkTree(node[1])
                return self.walkTree(node[2])

        if node[0] == 'for_loop':
            variables = self.env.copy()
            variablesBack = self.prevEnv.copy()
            setup = self.walkTree(node[1])
            condition = self.walkTree(node[2])
            if not isinstance(condition, bool):
                self.error("If statement condition must be of type 'bool'. Line: %d" % node[4])
            while(condition):
                self.walkTree(node[4])
                self.walkTree(node[3])
                condition = self.walkTree(node[2])

            variablesBack2 = self.prevEnv.copy()
            for key in variablesBack2:
                if key not in variablesBack:
                    del self.prevEnv[key]
            variables2 = self.env.copy()
            for key in variables2:
                if key not in variables:
                    del self.env[key]

        if (node[0] == 'while_loop'):
            variables = self.env.copy()
            condition = self.walkTree(node[1])
            if not isinstance(condition, bool):
                self.error("While loop condition must be of type 'bool'. Line: %d" % node[3])
            while (condition):
                self.walkTree(node[2])
                condition = self.walkTree(node[1])

            variablesBack2 = self.prevEnv.copy()
            for key in variablesBack2:
                if key not in variablesBack:
                    del self.prevEnv[key]
            variables2 = self.env.copy()
            for key in variables2:
                if key not in variables:
                    del self.env[key]

        if node[0] == 'if_stmt':
            variables = self.env.copy()
            variablesBack = self.prevEnv.copy()
            condition = self.walkTree(node[1])
            if not isinstance(condition, bool):
                self.error("If statement condition must be of type 'bool'. Line: %d" % node[4])
            if condition:
                self.walkTree(node[2])
            elif node[3] != None:
                self.walkTree(node[3])

            variablesBack2 = self.prevEnv.copy()
            for key in variablesBack2:
                if key not in variablesBack:
                    del self.prevEnv[key]
            variables2 = self.env.copy()
            for key in variables2:
                if key not in variables:
                    del self.env[key]

        if node[0] == 'func_def':
            self.env[node[2]] = ("function", node[1], self.walkTree(node[3]), node[4])

        if node[0] == 'func_call':
            vals = None
            length = 0
            if (node[2] != None):
                vals = self.walkTree(node[2])
                length = len(vals)

            if node[1] == "Previous" and isinstance(node[2], str):
                try:
                    return self.prevEnv[node[2]][2]
                except LookupError:
                    self.error("Variable '" + node[2] + "' undeclared or variable is static. Line %d." % node[3])
                    return 0

            if node[1] == 'PrintLine':
                if length != 1:
                    self.error("Wrong number of arguments for function 'PrintLine'. Line: %d" % node[3])
                value = self.walkTree(vals[0])
                if not isinstance(value, str):
                    self.error("First argument in function 'PrintLine' must be of type 'word' or 'letter'. Line: %d" % node[3])
                print(value)
                return None

            if node[1] == 'Print':
                if length != 1:
                    self.error("Wrong number of arguments for function 'Print'. Line: %d" % node[3])
                value = self.walkTree(vals[0])
                if not isinstance(value, str):
                    self.error("First argument in function 'Print' must be of type 'word' or 'letter'. Line: %d" % node[3])
                print(value, end='')
                return None

            if node[1] == 'ConvertToWord':
                if length != 1:
                    self.error("Wrong number of arguments for function 'ConvertToWord'. Line: %d" % node[3])
                value = self.walkTree(vals[0])
                elName = vals[0][1]
                if isinstance(value, str):
                    self.error("'" + elName + "' in function 'ConvertToWord' is already type of 'word'. Line: %d" % node[3])
                return str(value)

            if node[1] == 'ConvertToLetter':
                if length != 1:
                    self.error("Wrong number of arguments for function 'ConvertToLetter'. Line: %d" % node[3])
                value = self.walkTree(vals[0])
                elName = vals[0][1]
                print(value)
                if isinstance(value, str) and len(value) == 1:
                    self.error("'" + elName + "' in function 'ConvertToLetter' is already type of 'letter'. Line: %d" % node[3])
                elif (isinstance(value, str) and len(value) > 1) or (isinstance(value, int) and value > 9):
                    self.error("'" + elName + "' in function 'ConvertToLetter' can't be converted to 'letter'. Line: %d" % node[3])
                elif isinstance(value, float):
                    self.error("'" + elName + "' in function 'ConvertToLetter' can't be converted to 'real'. Line: %d" % node[3])
                return str(value)


            if node[1] == 'ConvertToNumber':
                if length != 1:
                    self.error("Wrong number of arguments for function 'ConvertToNumber'. Line: %d" % node[3])
                value = self.walkTree(vals[0])
                elName = vals[0][1]
                if isinstance(value, int):
                    self.error("'" + elName + "' in function 'ConvertToNumber' is already type of 'number'. Line: %d" % node[3])
                try:
                    int(value)
                except ValueError:
                    self.error("String can't be converted to number. Line: %d" % node[3])
                return int(value)

            if node[1] == 'ConvertToReal':
                if length != 1:
                    self.error("Wrong number of arguments for function 'ConvertToReal'. Line: %d" % node[3])
                value = self.walkTree(vals[0])
                elName = vals[0][1]
                if isinstance(value, float):
                    self.error("'" + elName + "' in function 'ConvertToReal' is already type of 'real'. Line: %d" % node[3])
                try:
                    int(value)
                except ValueError:
                    self.error("String can't be converted to real. Line: %d" % node[3])
                return int(value)

            # conflict_variables = {}
            # confilctBack_variables = {}
            # variables = self.env.copy()
            try:
                conflict_variables = {}
                variables = self.env.copy()

                if node[2] != None:
                    vars = self.env[node[1]][2]
                    var_length = len(vars)
                    if (var_length != length):
                        self.error("Wrong number of arguments for function '" + node[1] + ";. Line %d" % node[3])
                    for i in range(length):
                        typeValue = vars[i][1]
                        value = self.walkTree(vals[i])
                        if (isinstance(value, int) & (typeValue == 'number')) | (
                                isinstance(value, float) & (typeValue == 'real')) | (
                                isinstance(value, str) & (typeValue == 'word')) | (
                                isinstance(value, str) & (typeValue == 'letter')):
                            conflict_var = vars[i][2]
                            if conflict_var in self.env:
                                conflict_variables[conflict_var] = self.env[conflict_var]
                                del self.env[conflict_var]
                            self.env[self.walkTree(vars[i])] = ('var', typeValue, value)
                        else:
                            self.error("%d argument for function '%s' must be of type '%s'. Line %d" % (
                            i, node[1], typeValue, node[3]))
                function = self.env[node[1]]
                result = self.walkTree(function[3])
                if result[0] == 'return':
                    functionType = function[1]
                    if not ((isinstance(value, int) & (typeValue == 'number')) | (
                                    isinstance(value, float) & (typeValue == 'real')) | (
                                    isinstance(value, str) & (typeValue == 'word')) | (
                                    isinstance(value, str) & (typeValue == 'letter'))):
                        self.error("Return type does not match returning value in function '" + node[1] + "'")
                    variables2 = self.env.copy()
                    for key in variables2:
                        if key not in variables:
                            del self.env[key]
                    for key in conflict_variables:
                        self.env[key] = conflict_variables[key]
                    return result[1]
                self.error("function '%s' doesnt return anything. Line: %d" % (node[1], node[3]))
            except LookupError:
                self.error("Undefined function '%s'. Line: %d" % (node[1], node[3]))
                return None

        if node[0] == 'func_call_var':
            var = node
            vars = (node[1],)
            while var[2] != None:
                vars = vars + (var[2][1],)
                var = var[2]
            return vars

        if node[0] == 'func_var':
            var = node
            vars = (node[1],)
            while var[2] != None:
                vars = vars + (var[2][1],)
                var = var[2]
            return vars

        if node[0] == 'expr':
            value1 = self.walkTree(node[2])
            value2 = self.walkTree(node[3])
            if node[1] == '+':
                if (isinstance(value1, int) & isinstance(value2, int)) | (isinstance(value1, float) & isinstance(value2, float)) | (isinstance(value1, str) & isinstance(value2, str)):
                    return value1 + value2
                else:
                    self.error("'+' modifier can only be applied to types 'number' & 'number' or 'word' & 'word' or 'letter' & 'letter'. Line: %d" % node[4])
            elif node[1] == '-':
                if (isinstance(value1, int) and isinstance(value2, int)) or (isinstance(value1, float) and isinstance(value2, float)):
                    return value1 - value2
                else:
                    self.error("'-' modifier can only be applied to types 'number' & 'number'. Line: %d" % node[4])
            elif node[1] == '*':
                if (isinstance(value1, int) & isinstance(value2, int)) | (isinstance(value1, float) & isinstance(value2, float)):
                    return value1 * value2
                else:
                    self.error("'(' modifier can only be applied to types 'number' & 'number'. Line: %d" % node[4])
            elif node[1] == '/':
                if (isinstance(value1, int) & isinstance(value2, int)) | (isinstance(value1, float) & isinstance(value2, float)):
                    return value1 / value2
                else:
                    self.error("'/' modifier can only be applied to types 'number' & 'number'. Line: %d" % node[4])
            elif node[1] == '!=':
                return value1 != value2
            elif node[1] == '==':
                return value1 == value2
            elif node[1] == '>':
                if (isinstance(value1, int) & isinstance(value2, int)) | (isinstance(value1, str) & isinstance(value2, str)) | (isinstance(value1, float) & isinstance(value2, float)):
                    return value1 > value2
                else:
                    self.error("'>' modifier can only be applied to types 'number' & 'number' or 'word' & 'word' or 'letter' & 'letter'. Line: %d" % node[4])
            elif node[1] == '<':
                if (isinstance(value1, int) & isinstance(value2, int)) | (isinstance(value1, str) & isinstance(value2, str)) | (isinstance(value1, float) & isinstance(value2, float)):
                    return value1 < value2
                else:
                    self.error("'<' modifier can only be applied to types 'number' & 'number' or 'word' & 'word' or 'letter' & 'letter'. Line: %d" % node[4])
            elif node[1] == '<=':
                if (isinstance(value1, int) & isinstance(value2, int)) | (isinstance(value1, str) & isinstance(value2, str)) | (isinstance(value1, float) & isinstance(value2, float)):
                    return value1 <= value2
                else:
                    self.error("'<=' modifier can only be applied to types 'number' & 'number' or 'word' & 'word' or 'letter' & 'letter'. Line: %d" % node[4])
            elif node[1] == '>=':
                if (isinstance(value1, int) & isinstance(value2, int)) | (isinstance(value1, str) & isinstance(value2, str)) | (isinstance(value1, float) & isinstance(value2, float)):
                    return value1 >= value2
                else:
                    self.error("'>=' modifier can only be applied to types 'number' & 'number' or 'word' & 'word' or 'letter' & 'letter'. Line: %d" % node[4])
            elif node[1] == '||':
                if isinstance(value1, bool) & isinstance(value2, bool):
                    return value1 | value2
                else:
                    self.error("'||' modifier can only be applied to types 'bool' & 'bool'. Line: %d" % node[4])
            elif node[1] == '&&':
                if isinstance(value1, bool) & isinstance(value2, bool):
                    return value1 & value2
                else:
                    self.error("'&&' modifier can only be applied to types 'bool' & 'bool'. Line: %d" % node[4])

        if (node[0] == 'numberValue') | (node[0] == 'wordValue') | (node[0] == 'letterValue') | (node[0] == 'realValue'):
            return node[1]

        if node[0] == 'var_assign':
            var = self.walkTree(node[1])
            value = self.walkTree(node[2])
            # print(self.env[var])
            # print(value)
            isStatic = self.env[var][3]
            # print(isStatic)
            if isStatic == False:
                try:
                    varType = self.env[var][1]
                    if (varType == 'number') & isinstance(value, int):
                        self.prevEnv[var] = self.env[var]
                        self.env[var] = ('var', varType, value, False)
                    elif (varType == 'real') & isinstance(value, float):
                        self.prevEnv[var] = self.env[var]
                        self.env[var] = ('var', varType, value, False)
                    elif (varType == 'word') & isinstance(value, str):
                        self.prevEnv[var] = self.env[var]
                        self.env[var] = ('var', varType, value, False)
                    elif (varType == 'letter') & isinstance(value, str):
                        self.prevEnv[var] = self.env[var]
                        self.env[var] = ('var', varType, value, False)
                    else: self.error("Value assigned to variable of wrong type. Line: %d." % node[3])
                except LookupError:
                    self.error("Variable '" + var + "' undeclared. Line %d." % node[3])
                return ('var', varType, value)
            elif isStatic == True and self.env[var][4] == False:
                try:
                    varType = self.env[var][1]
                    if (varType == 'number') & isinstance(value, int):
                        self.env[var] = ('var', varType, value, True, True)
                    elif (varType == 'real') & isinstance(value, float):
                        self.env[var] = ('var', varType, value, True, True)
                    elif (varType == 'word') & isinstance(value, str):
                        self.env[var] = ('var', varType, value, True, True)
                    elif (varType == 'letter') & isinstance(value, str):
                        self.env[var] = ('var', varType, value, True, True)
                    else: self.error("Value assigned to variable of wrong type. Line: %d." % node[3])
                except LookupError:
                    self.error("Variable '" + var + "' undeclared. Line %d." % node[3])
                return ('var', varType, value)
            elif isStatic == True and self.env[var][4] == True:
                self.error("Static variable '" + var + "' can't be changed. Line %d." % node[3])

        if node[0] == 'var_declare':
            key = node[2]
            if node[3] == False:
                if (node[1] == 'number'):
                    self.env[node[2]] = ('var', 'number', 0, False)
                    self.prevEnv[node[2]] = ('previousVar', 'number', 0)
                elif (node[1] == 'real'):
                    self.env[node[2]] = ('var', 'real', 0.0, False)
                    self.prevEnv[node[2]] = ('previousVar', 'real', 0.0)
                elif (node[1] == 'word'):
                    self.env[node[2]] = ('var', 'word', "", False)
                    self.prevEnv[node[2]] = ('previousVar', 'word', "")
                elif (node[1] == 'letter'):
                    self.env[node[2]] = ('var', 'letter', '', False)
                    self.prevEnv[node[2]] = ('previousVar', 'letter', "")
                return node[2]
            else:
                if (node[1] == 'number'):
                    self.env[node[2]] = ('var', 'number', 0, True, False)
                elif (node[1] == 'real'):
                    self.env[node[2]] = ('var', 'real', 0.0, True, False)
                elif (node[1] == 'word'):
                    self.env[node[2]] = ('var', 'word', "", True, False)
                elif (node[1] == 'letter'):
                    self.env[node[2]] = ('var', 'letter', '', True, False)
                return node[2]

        if node[0] == 'var':
            try:
                return self.env[node[1]][2]
            except LookupError:
                self.error("Variable '" + node[1] + "' undeclared. Line %d." % node[2])
                return 0

    def error(self, string):
        print("Error: %s" % string)
        main()


def main():

    env = {}
    prevEnv = {}
    lexer = SppLexer()
    parser = SppParser()
    text = None
    choice = None
    try:
        text = open(sys.argv[1]).read()
        if text:
            tree = parser.parse(lexer.tokenize(text))
            print(tree)
            SppExecute(tree, env, prevEnv)
    except (IndexError, FileNotFoundError) as e:
        while ((choice != 'F') & (choice != 'L') & (choice != 'Q')):
            choice = input("Input file (F), command lines (L) or quit (Q)?: ")
        if choice == 'F':
            while True:
                try:
                    text = open(input('Input file: ')).read()
                    if text:
                        if text == 'Q':
                            sys.exit()
                        tree = parser.parse(lexer.tokenize(text))
                        SppExecute(tree, env, prevEnv)
                        main()
                except FileNotFoundError:
                    print("File not found, try again.")
        elif choice == 'L':
            while True:
                text = input('s++ > ')
                if text:
                    if text == 'Q':
                        sys.exit()
                    tree = parser.parse(lexer.tokenize(text))
                    print(tree)
                    SppExecute(tree, env, prevEnv)



if __name__ == '__main__':
    main()