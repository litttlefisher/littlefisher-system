package com.littlefisher.core.mybatis.generator.plugins;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.littlefisher.core.stereotype.enums.EnumBool;
import com.littlefisher.core.mybatis.generator.LittleFisherCommentGenerator;
import com.littlefisher.core.utils.CollectionUtil;
import com.littlefisher.core.utils.StringUtil;

import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Description:
 *
 * Created on 2017年3月4日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
public class MapperPlugin extends PluginAdapter {

    /**
     * mappers
     */
    private Set<String> mappers = Sets.newHashSet();

    /**
     * caseSensitive
     */
    private boolean caseSensitive = false;

    /**
     * 开始的分隔符，例如mysql为`，sqlserver为[
     */
    private String beginningDelimiter = "";

    /**
     * 结束的分隔符，例如mysql为`，sqlserver为]
     */
    private String endingDelimiter = "";

    /**
     * 数据库模式
     */
    private String schema;

    /**
     * 注释生成器
     */
    private CommentGeneratorConfiguration commentCfg;

    /**
     * author
     */
    private String author;

    /**
     * currentDateStr
     */
    private String currentDateStr;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        // 设置默认的注释生成器
        commentCfg = new CommentGeneratorConfiguration();
        commentCfg.setConfigurationType(LittleFisherCommentGenerator.class.getCanonicalName());
        context.setCommentGeneratorConfiguration(commentCfg);
        context.getProperties().forEach((key, value) -> commentCfg.addProperty((String)key, (String)value));
        // 支持oracle获取注释#114
        if (context.getJdbcConnectionConfiguration() != null) {
            context.getJdbcConnectionConfiguration().addProperty("remarksReporting", EnumBool.TRUE.getCode().toLowerCase());
        } else {
            context.getConnectionFactoryConfiguration().addProperty("remarksReporting", EnumBool.TRUE.getCode().toLowerCase());
        }
    }

    @Override
    public void setProperties(Properties properties) {
        this.context.getProperties().forEach(properties::put);
        super.setProperties(properties);
        String mappers = this.properties.getProperty("mappers");
        if (StringUtility.stringHasValue(mappers)) {
            this.mappers.addAll(Splitter.on(',').splitToList(mappers));
        } else {
            throw new MapperException("Mapper插件缺少必要的mappers属性!");
        }
        String caseSensitive = this.properties.getProperty("caseSensitive");
        if (StringUtility.stringHasValue(caseSensitive)) {
            this.caseSensitive = EnumBool.TRUE.getCode().equalsIgnoreCase(caseSensitive);
        }
        String beginningDelimiter = this.properties.getProperty("beginningDelimiter");
        if (StringUtility.stringHasValue(beginningDelimiter)) {
            this.beginningDelimiter = beginningDelimiter;
        }
        String endingDelimiter = this.properties.getProperty("endingDelimiter");
        if (StringUtility.stringHasValue(endingDelimiter)) {
            this.endingDelimiter = endingDelimiter;
        }
        String schema = this.properties.getProperty("schema");
        if (StringUtility.stringHasValue(schema)) {
            this.schema = schema;
        }
        String authorString = this.properties.getProperty("author");
        if (StringUtility.stringHasValue(authorString)) {
            author = authorString;
        }
        currentDateStr = new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
    }

    private String getDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        if (StringUtility.stringHasValue(schema)) {
            nameBuilder.append(schema);
            nameBuilder.append(".");
        }
        nameBuilder.append(beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(endingDelimiter);
        return nameBuilder.toString();
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 生成的Mapper接口
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        // 获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // import接口
        mappers.forEach(mapper -> {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
        });
        // import实体类
        interfaze.addImportedType(entityType);

        // 加注释
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * Description: " + introspectedTable.getFullyQualifiedTable() + " Mapper 接口");
        interfaze.addJavaDocLine(" *");
        interfaze.addJavaDocLine(" * Created on " + currentDateStr);
        interfaze.addJavaDocLine(" * @author " + author);
        interfaze.addJavaDocLine(" * @version 1.0");
        interfaze.addJavaDocLine(" * @since v1.0");
        interfaze.addJavaDocLine(" */");
        return true;
    }

    /**
     * 处理实体类的包和@Table注解
     */
    private void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 导入Table注解
        importClassTableAnnotation(topLevelClass, introspectedTable);
        // 引入JPA注解
        importFieldAnnotation(topLevelClass, Column.class);
        // 判断是否有@GeneratedValue注解的字段
        importFieldAnnotation(topLevelClass, GeneratedValue.class, GenerationType.class);
        // 导入主键生成策略
        importFieldAnnotation(topLevelClass, Id.class);
        // 判断field是否是Transient，如果是则导入对应包
        importFieldAnnotation(topLevelClass, Transient.class);
        // 导入列类型
        importFieldAnnotation(topLevelClass, ColumnType.class, JdbcType.class);

        // 构造Builder内部类
        addBuilderInnerClass(topLevelClass, introspectedTable);

        // 实体类注释
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * Description: " + introspectedTable.getFullyQualifiedTable() + " 实体");
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * Created on " + currentDateStr);
        topLevelClass.addJavaDocLine(" * @author " + author);
        topLevelClass.addJavaDocLine(" * @version 1.0");
        topLevelClass.addJavaDocLine(" * @since v1.0");
        topLevelClass.addJavaDocLine(" */");
    }

    /**
     * 创建Builder内部类
     * 
     * @param topLevelClass topLevelClass
     * @param introspectedTable introspectedTable
     */
    private void addBuilderInnerClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        InnerClass builder = new InnerClass(new FullyQualifiedJavaType("Builder"));
        builder.setVisibility(JavaVisibility.PUBLIC);
        builder.setStatic(true);

        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        // 构造参数
        builder.addField(buildInstanceField(entityType));

        // 空参数的私有构造函数
        builder.addMethod(buildConstructor());

        // 空入参getInstance
        builder.addMethod(buildGetInstance(entityType, true));
        // 有实体入参的getInstance
        builder.addMethod(buildGetInstance(entityType, false));
        // 构造add方法
        introspectedTable.getAllColumns()
            .forEach(introspectedColumn -> builder.addMethod(buildAddMethod(introspectedColumn)));
        // 构造build方法
        builder.addMethod(buildBuildMethod(entityType));

        topLevelClass.addInnerClass(builder);
    }

    /**
     * 构造getInstance方法
     * 
     * @param entityType 实体
     * @return Method
     */
    private Method buildGetInstance(FullyQualifiedJavaType entityType, boolean emptyParameter) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setStatic(true);
        method.setReturnType(new FullyQualifiedJavaType("Builder"));
        method.setName("getInstance");
        method.addBodyLine("Builder builder = new Builder();");
        if (!emptyParameter) {
            method.addParameter(new Parameter(entityType, "instance"));
            method.addBodyLine("builder.instance = instance;");
        } else {
            method.addBodyLine("builder.instance = new " + entityType.getShortNameWithoutTypeArguments() + "();");
        }
        method.addBodyLine("return builder;");
        return method;
    }

    /**
     * 构造build方法
     * 
     * @param entityType 实体
     * @return Method
     */
    private Method buildBuildMethod(FullyQualifiedJavaType entityType) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(entityType);
        method.setName("build");
        method.addBodyLine("return this.instance;");
        return method;
    }

    /**
     * 构造field参数
     * 
     * @param entityType 实体
     * @return Field
     */
    private Field buildInstanceField(FullyQualifiedJavaType entityType) {
        Field field = new Field();
        field.setName("instance");
        field.setType(entityType);
        field.setVisibility(JavaVisibility.PRIVATE);
        return field;
    }

    /**
     * 构造addXxx方法
     * 
     * @param introspectedColumn 列信息
     * @return Method
     */
    private Method buildAddMethod(IntrospectedColumn introspectedColumn) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType("Builder"));
        method.setName("add" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, introspectedColumn.getJavaProperty()));
        method.addParameter(
            new Parameter(introspectedColumn.getFullyQualifiedJavaType(), introspectedColumn.getJavaProperty()));
        method.addBodyLine("this.instance.set"
            + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, introspectedColumn.getJavaProperty()) + "("
            + introspectedColumn.getJavaProperty() + ");");
        method.addBodyLine("return this;");
        return method;
    }

    /**
     * 创建构造函数
     * 
     * @return 生成的方法
     */
    private Method buildConstructor() {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PRIVATE);
        method.setName("Builder");
        method.setConstructor(true);
        method.addBodyLine("super();");
        return method;
    }

    /**
     * 导入Table注解
     * 
     * @param topLevelClass topLevelClass
     * @param introspectedTable introspectedTable
     */
    private void importClassTableAnnotation(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 引入JPA注解
        topLevelClass.addImportedType("javax.persistence.Table");
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        // 如果包含空格，或者需要分隔符，需要完善
        if (StringUtility.stringContainsSpace(tableName)) {
            tableName = context.getBeginningDelimiter() + tableName + context.getEndingDelimiter();
        }
        // 是否忽略大小写，对于区分大小写的数据库，会有用
        if (caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (StringUtility.stringHasValue(schema) || StringUtility.stringHasValue(beginningDelimiter)
            || StringUtility.stringHasValue(endingDelimiter)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        }
    }

    /**
     * field上的注解，需要在class上进行导入
     * 
     * @param topLevelClass topLevelClass
     * @param mainAnnotation 字段上的注解
     * @param extAnnotations 该注解所需额外的导入类
     */
    private void importFieldAnnotation(TopLevelClass topLevelClass, Class<?> mainAnnotation,
        Class<?>... extAnnotations) {
        boolean hasAnnotationOnField = Iterators.any(topLevelClass.getFields().iterator(),
            field -> field != null && CollectionUtil.isNotEmpty(field.getAnnotations())
                && Iterators.any(field.getAnnotations().iterator(), annotation -> StringUtil.isNotBlank(annotation)
                    && annotation.startsWith("@" + mainAnnotation.getSimpleName())));
        if (hasAnnotationOnField) {
            topLevelClass.addImportedType(mainAnnotation.getCanonicalName());
            if (ArrayUtils.isNotEmpty(extAnnotations)) {
                for (Class<?> extAnnotation : extAnnotations) {
                    topLevelClass.addImportedType(extAnnotation.getCanonicalName());
                }
            }
        }
    }

    /**
     * 生成基础实体类
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 生成实体类注解KEY对象
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 生成带BLOB字段的对象 XxxWithBlobs
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /*下面所有return false的方法都不生成。这些都是基础的CRUD方法，使用通用Mapper实现*/

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerApplyWhereMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    // 以下方法，关闭所有跟Example相关的count、update、delete、select方法，这些方法在通用Mapper中已经存在

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return true;
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        return false;
    }

}