#coding:utf-8

# Create your views here.
from django.shortcuts import render_to_response, redirect

from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse
from django.template.context import RequestContext

from nucleo.models import Smell, Project, Tool, Human, Statement, ToolAnalysis, HumanAnalysis, Metric, MeasureCalculator, Measure


def index(request):
    return redirect("/home")

def home(request):
    return render_to_response("index.html", context_instance=RequestContext(request))

def smells(request):
    return render_to_response("smells.html", {'smells':Smell.objects.all()}, context_instance=RequestContext(request))

def projects(request):
    return render_to_response("projects.html", {'projects':Project.objects.all()}, context_instance=RequestContext(request))

def statements(request):
    return render_to_response("statements.html", {'statements':Statement.objects.all()}, context_instance=RequestContext(request))

def tools(request):
    return render_to_response("tools.html", {'tools':Tool.objects.all()}, context_instance=RequestContext(request))

def tool_analysis(request):
    return render_to_response("tool_analysis.html", {'analysis':ToolAnalysis.objects.all()}, context_instance=RequestContext(request))

def humans(request):
    return render_to_response("humans.html", {'humans':Human.objects.all()}, context_instance=RequestContext(request))

def human(request, human_id):
    human = Human.objects.get(id=human_id)
    return render_to_response("human.html", {'human':human}, context_instance=RequestContext(request))

def human_analysis(request):
    return render_to_response("human_analysis.html", {'analysis':HumanAnalysis.objects.all()}, context_instance=RequestContext(request))

def report_precision(request):
    return render_to_response("report_precision.html", {'humans':Human.objects.all()}, context_instance=RequestContext(request))

def metrics(request):
    return render_to_response("metrics.html", {'metrics':Metric.objects.all()}, context_instance=RequestContext(request))

def calculators(request):
    return render_to_response("calculators.html", {'calculators':MeasureCalculator.objects.all()}, context_instance=RequestContext(request))

def measures(request):
    return render_to_response("measures.html", {'statements':Statement.objects.all(),
                                                'metrics':Metric.objects.all()}, context_instance=RequestContext(request))
    
def god_class(request):
    return render_to_response("god_class.html", 
                    {'stats':Statement.objects.filter(humananalysis__smell__name="God Class").distinct()}, 
                     context_instance=RequestContext(request))
        