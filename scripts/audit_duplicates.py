#!/usr/bin/env python3
import os
import re
import json
import hashlib
from collections import defaultdict

IGNORE_DIRS = {'.git', '.mvn', 'target', '.idea', '.settings', 'node_modules'}
ROOT = '.'


def walk_files(root=ROOT):
    out = []
    for dp, dns, fns in os.walk(root):
        dns[:] = [d for d in dns if d not in IGNORE_DIRS]
        for f in fns:
            p = os.path.join(dp, f)
            if p.startswith('./'):
                p = p[2:]
            out.append(p)
    return sorted(out)


def dup_by_name(files):
    by = defaultdict(list)
    for p in files:
        by[os.path.basename(p)].append(p)
    return {k: sorted(v) for k, v in by.items() if len(v) > 1}


def dup_by_content(files):
    by = defaultdict(list)
    for p in files:
        try:
            with open(p, 'rb') as fh:
                h = hashlib.sha256(fh.read()).hexdigest()
            by[h].append(p)
        except Exception:
            continue
    return {h: sorted(v) for h, v in by.items() if len(v) > 1}


def java_simple_name_dups(files):
    javas = [p for p in files if p.endswith('.java') and p.startswith('src/main/java/')]
    by = defaultdict(list)
    for p in javas:
        by[os.path.splitext(os.path.basename(p))[0]].append(p)
    return {k: sorted(v) for k, v in by.items() if len(v) > 1}


def thymeleaf_name_dups(files):
    tpls = [p for p in files if p.startswith('src/main/resources/templates/') and p.endswith('.html')]
    by = defaultdict(list)
    for p in tpls:
        by[os.path.basename(p)].append(p)
    return {k: sorted(v) for k, v in by.items() if len(v) > 1}


def static_name_dups(files):
    static = [p for p in files if p.startswith('src/main/resources/static/')]
    by = defaultdict(list)
    for p in static:
        by[os.path.basename(p)].append(p)
    return {k: sorted(v) for k, v in by.items() if len(v) > 1}


def test_name_dups(files):
    tests = [p for p in files if p.startswith('src/test/')]
    by = defaultdict(list)
    for p in tests:
        by[os.path.basename(p)].append(p)
    return {k: sorted(v) for k, v in by.items() if len(v) > 1}


def mapping_and_bean_dups(files):
    java = [p for p in files if p.endswith('.java') and p.startswith('src/main/java/')]
    ann_pat = re.compile(r'@(RequestMapping|GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\s*\(([^)]*)\)', re.S)
    class_pat = re.compile(r'\bclass\s+(\w+)')
    req_class_pat = re.compile(r'@RequestMapping\s*\(([^)]*)\)', re.S)
    ster_pat = re.compile(r'@(Controller|RestController|Service|Repository|Component)\s*(\(([^)]*)\))?')

    map_entries = []
    bean_names = defaultdict(list)

    for p in java:
        txt = open(p, encoding='utf-8').read()
        cls_m = class_pat.search(txt)
        cls = cls_m.group(1) if cls_m else os.path.splitext(os.path.basename(p))[0]

        for sm in ster_pat.finditer(txt):
            args = sm.group(3) or ''
            named = re.search(r'value\s*=\s*"([^"]+)"', args) or re.search(r'"([^"]+)"', args)
            bean_name = named.group(1) if named else cls[0].lower() + cls[1:]
            bean_names[bean_name].append(p)
            break

        if '@Controller' in txt or '@RestController' in txt:
            before_class = txt[:txt.find('class')] if 'class' in txt else txt
            base = ''
            cm = req_class_pat.search(before_class)
            if cm:
                args = cm.group(1)
                pm = re.search(r'(?:value|path)\s*=\s*"([^"]+)"', args) or re.search(r'"([^"]+)"', args)
                if pm:
                    base = pm.group(1)

            for am in ann_pat.finditer(txt):
                ann, args = am.group(1), am.group(2)
                pm = re.search(r'(?:value|path)\s*=\s*"([^"]+)"', args) or re.search(r'"([^"]+)"', args)
                path = pm.group(1) if pm else ''
                method = {
                    'GetMapping': 'GET',
                    'PostMapping': 'POST',
                    'PutMapping': 'PUT',
                    'DeleteMapping': 'DELETE',
                    'PatchMapping': 'PATCH',
                    'RequestMapping': 'ANY',
                }[ann]
                mm = re.search(r'method\s*=\s*RequestMethod\.(\w+)', args)
                if mm:
                    method = mm.group(1)

                full = (base.rstrip('/') + '/' + path.lstrip('/')).replace('//', '/')
                if full == '':
                    full = '/'
                line = txt[:am.start()].count('\n') + 1
                map_entries.append((method, full, p, line))

    by_map = defaultdict(list)
    for method, path, file, line in map_entries:
        by_map[(method, path)].append({'file': file, 'line': line})

    return {
        'mapping_duplicates': {
            f'{k[0]} {k[1]}': v for k, v in by_map.items() if len(v) > 1
        },
        'bean_name_duplicates': {
            k: sorted(v) for k, v in bean_names.items() if len(v) > 1
        }
    }


def classpath_info(files):
    classpath_files = [f for f in files if f.endswith('.classpath')]
    return {
        'classpath_files_found': classpath_files,
        'can_validate_entries': bool(classpath_files)
    }


def main():
    files = walk_files()
    result = {
        'duplicate_file_names': dup_by_name(files),
        'duplicate_file_content_sha256': dup_by_content(files),
        'duplicate_java_simple_names': java_simple_name_dups(files),
        'duplicate_thymeleaf_template_names': thymeleaf_name_dups(files),
        'duplicate_static_resource_names': static_name_dups(files),
        'duplicate_test_file_names': test_name_dups(files),
        'classpath_analysis': classpath_info(files),
    }
    result.update(mapping_and_bean_dups(files))
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == '__main__':
    main()
