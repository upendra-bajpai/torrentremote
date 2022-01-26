from bs4 import BeautifulSoup
import urllib.request
import re
import json
import datetime
import os
import shutil
import datetime
import sys

#url_to='http://s8.bitdl.ir/Series/friends/S02/'
url_to=sys.argv[1]
print(url_to)
def parse_website(site_name):
	pass
	html_page = urllib.request.urlopen(site_name)
	soup = BeautifulSoup(html_page, "html.parser")
	links=soup.findAll('a', attrs={'href': re.compile(".mkv")})
	#print(links)
	lin=list(filter(None, links))
	datalink=[]
	for link in lin:
		print(link.get('href'))
		datalink.append(url_to+link['href'])
	#print(datalink)
	write_in_json(datalink)



def write_in_json(links):
	data = {}
	data['series']='_'.join(url_to.split('/')[-3:-2])
	data['details']=[]
	seasons={}
	episodes=[]
	seasons["season"]=int(''.join(url_to.split('S')[-1:])[:-1])
	for x in links:
		pass
		episodes.append({"name":x,"url":x,"thumbs":["url1","url2","url3"],"subtitle":"tilte"})
	seasons["episodes"]=episodes
	data["details"].append(seasons)
	fie='_'.join(url_to.split('/')[-3:])
	with open('%s.json' %fie, 'w') as outfile:
		json.dump(data, outfile)

parse_website(url_to)