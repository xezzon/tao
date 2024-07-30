grammar ODataOrderby;

import ODataLexer;

clause
  : expression (',' expression)*
  ;

expression
  : member=MEMBER SORT?
  ;
