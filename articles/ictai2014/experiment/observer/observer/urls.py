from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'chufal.views.home', name='home'),
    # url(r'^chufal/', include('chufal.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
    url(r'^$', 'nucleo.views.index'),
    url(r'^home$', 'nucleo.views.home'),
    url(r'^smells', 'nucleo.views.smells'),
    url(r'^projects', 'nucleo.views.projects'),
    url(r'^statements', 'nucleo.views.statements'),
    url(r'^tools', 'nucleo.views.tools'),
    url(r'^tool-analysis', 'nucleo.views.tool_analysis'),
    url(r'^humans', 'nucleo.views.humans'),
    url(r'^human/(?P<human_id>\d+)$', 'nucleo.views.human'),
    url(r'^human-analysis', 'nucleo.views.human_analysis'),
    url(r'^report_precision', 'nucleo.views.report_precision'),
    url(r'^metrics', 'nucleo.views.metrics'),
    url(r'^calculators', 'nucleo.views.calculators'),
    url(r'^measures', 'nucleo.views.measures'),
    url(r'^god_class', 'nucleo.views.god_class'),
    
)
