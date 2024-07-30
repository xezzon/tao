grammar ODataFilter;

import ODataLexer;

clause
  : OPEN clause CLOSE # Parenthesis
  | binary_operation # BinaryOperation
  | method_call # MethodCall
  | clause AND clause # AndClase
  | clause OR clause # OrClause
  | NOT clause # NotClause
  ;

binary_operation
  : member=MEMBER operation=EQ LITERAL # EqOperation
  | member=MEMBER operation=NE LITERAL # NeOperation
  | member=MEMBER operation=GT LITERAL # GtOperation
  | member=MEMBER operation=LT LITERAL # LtOperation
  | member=MEMBER operation=GE LITERAL # GeOperation
  | member=MEMBER operation=LE LITERAL # LeOperation
  | member=MEMBER operation=IN OPEN LITERAL (COMMA LITERAL)* CLOSE # InOperation
  ;

method_call
  : operation=CONTAINS OPEN member=MEMBER COMMA LITERAL CLOSE # ContainsMethodCall
  | operation=START_WITH OPEN member=MEMBER COMMA LITERAL CLOSE # StartsWithMethodCall
  | operation=END_WITH OPEN member=MEMBER COMMA LITERAL CLOSE # EndsWithMethodCall
  ;
