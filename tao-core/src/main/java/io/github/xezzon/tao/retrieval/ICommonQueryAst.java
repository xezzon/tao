package io.github.xezzon.tao.retrieval;

/**
 * 通用查询组件抽象语法树
 * @param <I> 抽象语法树实现类
 * @author xezzon
 */
public interface ICommonQueryAst<I> {

  /**
   * 将通用查询组件转换为抽象语法树
   * @param commonQuery 通用查询组件
   * @param initQuery 初始查询条件 需要实现方处理为null的情况
   * @return 抽象语法树实现类
   */
  I toAst(CommonQuery commonQuery, I initQuery);
}
