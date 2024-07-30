lexer grammar ODataLexer;

MEMBER: (LETTER | DIGIT | '_' | '.')+;
LITERAL: (BOOL | NUMBER | STRING);
OPEN: (SP)* '(' (SP)*;
CLOSE: (SP)* ')' (SP)*;
COMMA: (SP)* ',' (SP)*;

AND: (SP)+ 'and' (SP)+;
OR: (SP)+ 'or' (SP)+;
NOT: (SP)+ 'not' (SP)+;
EQ: (SP)+ 'eq' (SP)+;
NE: (SP)+ 'ne' (SP)+;
GT: (SP)+ 'gt' (SP)+;
LT: (SP)+ 'lt' (SP)+;
GE: (SP)+ 'ge' (SP)+;
LE: (SP)+ 'le' (SP)+;
IN: (SP)+ 'in' (SP)+;
CONTAINS: 'contains';
START_WITH: 'startswith';
END_WITH: 'endswith';

SORT: (SP)+ ('asc' | 'desc');

fragment LETTER: [A-Za-z];
fragment DIGIT: [0-9];
fragment INT: '0' | [1-9] [0-9]*;
fragment EXP: [Ee] [+\-]? INT;
fragment NUMBER: '-'? INT ('.' [0-9]+)? EXP?;
fragment BOOL: ('true' | 'false');
fragment STRING: '\'' .*? '\'';
fragment SP: ' ';
