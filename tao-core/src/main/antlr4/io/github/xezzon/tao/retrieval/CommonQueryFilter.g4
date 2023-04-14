/* 语法名 */
grammar CommonQueryFilter;
/* 词法规则 */
AND: 'AND';
OR: 'OR';
CONJ
  : AND
  | OR;
OP
  : 'EQ'
  | 'NE'
  | 'LLIKE'
  | 'IN'
  | 'OUT'
  | 'GT'
  | 'LT'
  | 'GE'
  | 'LE'
  | 'NULL';
VALUE: (BOOL | NUMBER | STRING);
FIELD: (LETTER | DIGIT | '_')+;
fragment BOOL: ('true' | 'false');
fragment LETTER: [A-Za-z];
fragment DIGIT: [0-9];
fragment INT: '0' | [1-9] [0-9]*;
fragment EXP: [Ee] [+\-]? INT;
fragment NUMBER: '-'? INT ('.' [0-9]+)? EXP?;
fragment STRING: '\'' .*? '\'';
WHITE_SPACE: (' '|'\r'|'\t'|'\u000C'|'\n') -> skip;
/* 语法规则 */
clause
  : '(' clause ')' # Parenthesis
  | column=FIELD compare=OP value=VALUE # Predicate
  | clause logic=AND clause # AndLogic
  | clause logic=OR clause # OrLogic
  ;
