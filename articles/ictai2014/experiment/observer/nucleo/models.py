from django.db import models
from django.db.models.aggregates import Sum
from jqgrid import JqGrid
import datetime

import grids
from django.core.urlresolvers import reverse_lazy

class Project(models.Model):
    name = models.CharField(max_length=30)
    description = models.CharField(max_length=100, blank=True)
    
    def __unicode__(self):
        return self.name

class Statement(models.Model):
    project = models.ForeignKey(Project)
    name = models.CharField(max_length=200)
    ref = models.CharField(max_length=20)
    obs = models.CharField(max_length=100,blank=True)
    line = models.IntegerField()
    
    def __unicode__(self):
        return self.name    
    
    def get_class_name(self):
        if "###" in self.name:
            return self.name.split("###")[0]
        return self.name

class Smell(models.Model):
    name = models.CharField(max_length=30)
    description = models.CharField(max_length=100, blank=True)
    short_name = models.CharField(max_length=10, blank=True)
    
    def __unicode__(self):
        return self.name    


class Tool(models.Model):
    name = models.CharField(max_length=30)
    description = models.CharField(max_length=100, blank=True)
    
    def __unicode__(self):
        return self.name    

class ToolAnalysis(models.Model):
    tool = models.ForeignKey(Tool)
    smell = models.ForeignKey(Smell)
    statement = models.ForeignKey(Statement)
    verified = models.BooleanField(default=True)
    date = models.DateTimeField(default=datetime.datetime.now())

    def __unicode__(self):
        return "%s - %s - %s" % (self.tool, self.smell.short_name, self.verified)    
    
class Human(models.Model):
    name = models.CharField(max_length=30)
    description = models.TextField( blank=True)
    
    def __unicode__(self):
        return self.name    
    
class HumanAnalysis(models.Model):
    human = models.ForeignKey(Human)
    smell = models.ForeignKey(Smell)
    statement = models.ForeignKey(Statement)
    verified = models.BooleanField(default=True)
    date = models.DateTimeField(default=datetime.datetime.now())
    
    def __unicode__(self):
        return "%s - %s - %s" % (self.human, self.smell.short_name, self.verified)
    
class Metric(models.Model):
    name = models.CharField(max_length=40)
    short_name = models.CharField(max_length=7)
    description = models.TextField(blank=True)

class MeasureCalculator(models.Model):
    name = models.CharField(max_length=30)
    version = models.CharField(max_length=30)
    description = models.CharField(max_length=100, blank=True)

class Measure(models.Model):
    calculator = models.ForeignKey(MeasureCalculator)
    metric = models.ForeignKey(Metric)
    value = models.FloatField()
    statement = models.ForeignKey(Statement)