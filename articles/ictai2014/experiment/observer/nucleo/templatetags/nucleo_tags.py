# coding:utf-8
'''
Created on Jan 17, 2013

@author: hozano
'''
from datetime import datetime
from django import template
from django.core.serializers import serialize
from django.db.models.query import QuerySet
from django.utils import simplejson
from django.utils.safestring import mark_safe
register = template.Library()

from nucleo.models import ToolAnalysis, Tool, Statement, Smell, Human, HumanAnalysis, Metric, Measure

@register.simple_tag
def navactive(request, names, extra=""):
    names = names.split(",")
    for name in names:
        if name and request.path.startswith("/"+name):
            return "class='active %s'" % extra
    return ""


VALUES = ['info', 'warning', 'important', 'success']
@register.simple_tag
def int2debug(number):
    if isinstance(number, int):
        return VALUES[number % 4]
    return VALUES[0]

@register.simple_tag
def toolanalysis(tool_name, stat_id, smell_id):
    smells = [smell.name for smell in Smell.objects.all()]
    if smell_id in smells:
        smell_id = Smell.objects.get(name=smell_id).id
    
    tool = Tool.objects.get(name=tool_name.strip())
    stat = Statement.objects.get(id=stat_id)
    smell = Smell.objects.get(id=smell_id)
    available_tools = Tool.objects.filter(toolanalysis__smell=smell)
    
    
    if tool in available_tools:
        anl = ToolAnalysis.objects.filter(tool=tool, statement=stat, smell=smell)
        if anl and anl[0].verified:
            return "<span class='badge badge-success'>Y</span>"
        else:
            return "<span class='badge badge-danger'>N</span>"
    return "<span class='badge badge-black'>X</span>"

@register.simple_tag
def comparetool(smell_name, human_id, tool_name, value=1, project_id=1):
    tool = Tool.objects.get(name=tool_name.strip())
    human = Human.objects.get(id=human_id)
    smell = Smell.objects.get(short_name=smell_name)
    available_tools = Tool.objects.filter(toolanalysis__smell=smell)
    value = int(value)
    project_id= int(project_id)
    
    
    if tool in available_tools:
        stats = Statement.objects.filter(project__id=project_id, toolanalysis__tool=tool, toolanalysis__smell=smell, toolanalysis__verified=value, humananalysis__human=human)
        hanls = HumanAnalysis.objects.filter(human=human, smell=smell, verified=value, statement__in=stats, statement__project__id=project_id)
        
        span = "<span class='label label-danger'>"
        if value:
            span = "<span class='label label-success'>"
            
        percent = stats.count() != 0 and (100.0*hanls.count()/stats.count()) or 0
        percent = ("%.2f" % percent) + " %"
        return "%s %s/%s (%s)</span>" % (span, hanls.count(), stats.count(), percent)
    return "<span class='badge badge-black'>X</span>"

@register.simple_tag
def human_analysis(human_id, smell_name="", project_id=1):
    human = Human.objects.get(id=human_id)
    
    if smell_name:
        smell = Smell.objects.get(short_name=smell_name)
        analysis = HumanAnalysis.objects.filter(statement__project__id=project_id, human=human, smell=smell)
        true_analysis = HumanAnalysis.objects.filter(human=human, smell=smell, verified=True, statement__project__id=project_id)
    else:
        analysis = HumanAnalysis.objects.filter(statement__project__id=project_id, human=human)
        true_analysis = HumanAnalysis.objects.filter(human=human, verified=True, statement__project__id=project_id)
    
    percent = "%.2f" % (100.0*true_analysis.count() / analysis.count())
    
    return "%s/%s (%s)" % (true_analysis.count(), analysis.count(), percent)

@register.simple_tag
def get_measure_value(stat_id, metric_id):
    metrics = [metric.short_name for metric in Metric.objects.all()]
    if metric_id in metrics:
        metric_id = Metric.objects.get(short_name=metric_id).id
    stat = Statement.objects.get(id=int(stat_id))
    metric = Metric.objects.get(id=int(metric_id))
    measures = Measure.objects.filter(statement=stat, metric=metric)
    
    if measures:
        return measures[0].value
    
    return "x"

@register.simple_tag
def lm_god_class(stat_id):
    stat = Statement.objects.get(id=stat_id)
    wmc = get_measure_value(stat.id, "WMC2")
    atfd = get_measure_value(stat.id, "ATFD")
    tcc = get_measure_value(stat.id, "TCC")
    
    WMC_VERY_HIGH = 47.0
    FEW_THRESHOLD = 5.0
    ONE_THIRD_THRESHOLD = 1.0/3.0
    
    tool = Tool.objects.filter(name="PMD")[0]
    smell = Smell.objects.get(name="God Class")

    
    if (wmc >= WMC_VERY_HIGH) and (atfd > FEW_THRESHOLD) and (tcc < ONE_THIRD_THRESHOLD):
        ToolAnalysis.objects.get_or_create(tool=tool, smell=smell, statement=stat, verified=True)
        return "<span class='badge badge-success'>Y</span>"
    
    ToolAnalysis.objects.get_or_create(tool=tool, smell=smell, statement=stat, verified=False)
    return "<span class='badge badge-danger'>N</span>"

@register.filter
def jsonify(obj):
    if isinstance(obj, QuerySet):
        return mark_safe(serialize('json', obj))
    return mark_safe(simplejson.dumps(obj))

jsonify.is_safe = True   

import locale
@register.filter
def currency(value, arg = '', symbol = False):
    '''
    Currency formatting template filter.

    Takes a number -- integer, float, decimal -- and formats it according to
    the locale specified as the template tag argument (arg). Examples:

      * {{ value|currency }}
      * {{ value|currency:"en_US" }}
      * {{ value|currency:"pt_BR" }}
      * {{ value|currency:"pt_BR.UTF8" }}

    If the argument is omitted, the default system locale will be used.

    The third parameter, symbol, controls whether the currency symbol will be
    printed or not. Defaults to true.

    As advised by the Django documentation, this template won't raise
    exceptions caused by wrong types or invalid locale arguments. It will
    return an empty string instead.

    Be aware that currency formatting is not possible using the 'C' locale.
    This function will fall back to 'en_US.UTF8' in this case.
    '''

    saved = '.'.join([x for x in locale.getlocale() if x]) or (None, None)
    given = arg and ('.' in arg and str(arg) or str(arg) + '.UTF-8')

    # Workaround for Python bug 1699853 and other possibly related bugs.
    if '.' in saved and saved.split('.')[1].lower() in ('utf', 'utf8'):
        saved = saved.split('.')[0] + '.UTF-8'

    if saved == (None, None) and given == '':
        given = 'en_US.UTF-8'

    try:
        locale.setlocale(locale.LC_ALL, given)

        return locale.currency(value or 0, symbol, True)

    except (TypeError, locale.Error):
        return ''

    finally:
        locale.setlocale(locale.LC_ALL, saved)
